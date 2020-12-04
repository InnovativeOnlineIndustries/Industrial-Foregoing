package com.buuz135.industrial.block.generator.tile;

import com.buuz135.industrial.block.generator.mycelial.IMycelialGeneratorType;
import com.buuz135.industrial.block.generator.mycelial.MycelialDataManager;
import com.buuz135.industrial.block.tile.IndustrialGeneratorTile;
import com.buuz135.industrial.module.ModuleGenerator;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ProgressBarScreenAddon;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MycelialReactorTile extends IndustrialGeneratorTile<MycelialReactorTile> {

    @Save
    private String owner;
    private ProgressBarComponent<MycelialReactorTile> bar;

    public MycelialReactorTile() {
        super(ModuleGenerator.MYCELIAL_REACTOR);
    }

    @Nonnull
    @Override
    public MycelialReactorTile getSelf() {
        return this;
    }

    @Override
    public int consumeFuel() {
        MycelialDataManager.getReactorAvailable(owner, this.world, true);
        return 5;
    }

    @Override
    public boolean canStart() {
        int amount = MycelialDataManager.getReactorAvailable(owner, this.world, false).size();
        if (amount == IMycelialGeneratorType.TYPES.size()){
            MycelialDataManager.getReactorAvailable(owner, this.world, true);
            return true;
        }
        markForUpdate();
        return false;
    }

    @Override
    public int getEnergyProducedEveryTick() {
        return 25_000_000;
    }

    @Override
    public ProgressBarComponent<MycelialReactorTile> getProgressBar() {
        bar = new ProgressBarComponent<MycelialReactorTile>(30, 20, 0, 100){
            @Override
            public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
                return Collections.singletonList(() -> new ProgressBarScreenAddon(30, 20, bar){
                    @Override
                    public List<ITextComponent> getTooltipLines() {
                        List<ITextComponent> tooltip = new ArrayList<>();
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.titanium.progressbar.progress").getString() +  TextFormatting.WHITE + new DecimalFormat().format(bar.getProgress()) + TextFormatting.GOLD + "/" + TextFormatting.WHITE + new DecimalFormat().format(bar.getMaxProgress())));
                        int progress = (bar.getMaxProgress() - bar.getProgress());
                        if (!bar.getIncreaseType()) progress = bar.getMaxProgress() - progress;
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "ETA: " + TextFormatting.WHITE + new DecimalFormat().format(Math.ceil(progress * bar.getTickingTime() / 20D / bar.getProgressIncrease())) + TextFormatting.DARK_AQUA + "s"));
                        tooltip.add(new StringTextComponent(TextFormatting.GOLD + new TranslationTextComponent("tooltip.industrialforegoing.generating").getString() +  TextFormatting.WHITE + new DecimalFormat().format(getEnergyProducedEveryTick()) + TextFormatting.DARK_AQUA+ " FE" + TextFormatting.GOLD + "/" + TextFormatting.WHITE +TextFormatting.DARK_AQUA+ "t"));
                        return tooltip;
                    }
                });
            }
        }
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN);
        return bar;
    }

    @Override
    public int getEnergyCapacity() {
        return 100_000_000;
    }

    @Override
    public int getExtractingEnergy() {
        return getEnergyCapacity();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ProgressBarComponent<MycelialReactorTile> getBar() {
        return bar;
    }
}
