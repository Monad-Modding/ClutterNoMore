package dev.tazer.clutternomore.block;

import dev.tazer.clutternomore.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.20.5 && <26.3 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}

import java.util.Optional;

public class WeatheringVerticalStairBlock extends VerticalStairBlock implements WeatheringCopper {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<WeatheringVerticalStairBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringVerticalStairBlock::getAge), propertiesCodec()).apply(instance, WeatheringVerticalStairBlock::new));
    //?}

    private final WeatherState age;

    public WeatheringVerticalStairBlock(WeatherState age, Properties properties) {
        super(properties);
        this.age = age;
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends VerticalStairBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        //? if >=1.21 {
        this.changeOverTime(state, level, pos, random);
        //?} else {
        /*this.onRandomTick(state, level, pos, random);
        *///?}
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return this.getNext(state).isPresent();
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return Services.PLATFORM.getNextOxidation(state.getBlock()).map(next -> next.withPropertiesOf(state));
    }

    @Override
    public WeatherState getAge() {
        return this.age;
    }
}
