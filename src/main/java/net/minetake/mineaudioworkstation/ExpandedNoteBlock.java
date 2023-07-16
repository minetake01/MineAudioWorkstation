package net.minetake.mineaudioworkstation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ExpandedNoteBlock extends Block {
    public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty NOTE = IntProperty.of("note", 0, 11);
    public static final IntProperty OCTAVE = IntProperty.of("octave", 0, 4);
    public static final BooleanProperty POSITIVE = BooleanProperty.of("positive");

    public ExpandedNoteBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(POWERED, false).with(NOTE, 0).with(OCTAVE, 0).with(POSITIVE, true));
    }

    private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
        Instrument instrument = world.getBlockState(pos.up()).getInstrument();
        if (instrument.isNotBaseBlock()) {
            return state.with(INSTRUMENT, instrument);
        }
        Instrument instrument2 = world.getBlockState(pos.down()).getInstrument();
        Instrument instrument3 = instrument2.isNotBaseBlock() ? Instrument.HARP : instrument2;
        return state.with(INSTRUMENT, instrument3);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean bl;
        boolean bl2 = bl = direction.getAxis() == Direction.Axis.Y;
        if (bl) {
            return this.getStateWithInstrument(world, pos, state);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(POWERED)) {
            if (bl) {
                this.playNote(null, state, world, pos);
            }
            world.setBlockState(pos, state.with(POWERED, bl), Block.NOTIFY_ALL);
        }
    }

    private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        world.addSyncedBlockEvent(pos, this, 0, 0);
        world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_CONTROL)) {
            if (state.get(OCTAVE) == 4) {
                state = state.cycle(POSITIVE);
            }
            state = state.cycle(OCTAVE);
        } else {
            state = state.cycle(NOTE);
        }
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        this.playNote(player, state, world, pos);
        player.incrementStat(Stats.TUNE_NOTEBLOCK);
        return ActionResult.CONSUME;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) {
            return;
        }
        this.playNote(player, state, world, pos);
        player.incrementStat(Stats.PLAY_NOTEBLOCK);
    }

    public static float getNotePitch(int note, int octave, boolean positive) {
        int i = positive ? 1 : -1;
        return (float)Math.pow(2.0, (double)( i * (note + octave * 12) - 12) / 12.0);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        RegistryEntry<SoundEvent> registryEntry;
        float f;
        Instrument instrument = state.get(INSTRUMENT);
        if (instrument.shouldSpawnNoteParticles()) {
            int note = state.get(NOTE);
            int octave = state.get(OCTAVE);
            boolean positive = state.get(POSITIVE);
            f = getNotePitch(note, octave, positive);
            world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)(note + octave * 11) / (double)(4 * 12) , 0.0, 0.0);
        } else {
            f = 1.0f;
        }
        if (instrument.hasCustomSound()) {
            Identifier identifier = this.getCustomSound(world, pos);
            if (identifier == null) {
                return false;
            }
            registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
        } else {
            registryEntry = instrument.getSound();
        }

        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0f, f, world.random.nextLong());
        return true;
    }

    @Nullable
    private Identifier getCustomSound(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.up());
        if (blockEntity instanceof SkullBlockEntity skullBlockEntity) {
            return skullBlockEntity.getNoteBlockSound();
        }
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INSTRUMENT, POWERED, NOTE, OCTAVE, POSITIVE);
    }
}
