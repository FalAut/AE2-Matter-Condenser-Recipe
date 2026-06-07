package com.falaut.ae2mcr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.menu.AEBaseMenu;
import appeng.menu.slot.AppEngSlot;

@Mixin(AppEngSlot.class)
public interface AppEngSlotAccessor {
    @Invoker("getMenu")
    AEBaseMenu ae2mcr$invokeGetMenu();
}
