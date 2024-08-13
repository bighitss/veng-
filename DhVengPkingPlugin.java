// DhVengPkingPlugin.java

package com.yourname.dhvengpking;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;
import net.runelite.api.widgets.WidgetInfo;

@PluginDescriptor(
    name = "DH Vengeance PKing",
    description = "Automates the DH Vengeance PKing combo.",
    tags = {"combat", "pking", "dh", "vengeance"}
)
public class DhVengPkingPlugin extends Plugin
{
    @Inject
    private Client client;

    private boolean vengCasted = false;
    private boolean armorEquipped = true;

    @Provides
    DhVengPkingConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DhVengPkingConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (shouldUnequipArmor() && armorEquipped)
        {
            unequipArmor();
            armorEquipped = false;
        }

        if (!vengCasted && armorEquipped)
        {
            castVengeance();
            vengCasted = true;
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event)
    {
        if (!armorEquipped && event.getActor() == client.getLocalPlayer())
        {
            equipArmor();
            vengCasted = false;
            armorEquipped = true;
            specialAttack();
        }
    }

    private boolean shouldUnequipArmor()
    {
        // Custom logic to determine if armor should be unequipped
        return true; // This could be based on player health or other conditions
    }

    private void unequipArmor()
    {
        client.invokeMenuAction("Remove", "", 1, 5, EquipmentInventorySlot.BODY.getSlotIdx(), 9764864);
        client.invokeMenuAction("Remove", "", 1, 5, EquipmentInventorySlot.LEGS.getSlotIdx(), 9764864);
    }

    private void equipArmor()
    {
        client.invokeMenuAction("Wear", "Dharok's platebody", ItemID.DHAROKS_PLATEBODY, WidgetInfo.EQUIPMENT.getGroupId(), 9764864);
        client.invokeMenuAction("Wear", "Dharok's platelegs", ItemID.DHAROKS_PLATELEGS, WidgetInfo.EQUIPMENT.getGroupId(), 9764864);
    }

    private void castVengeance()
    {
        client.invokeMenuAction("Cast", "Vengeance", ItemID.RUNE_POUCH, WidgetInfo.SPELL_VENGEANCE.getId(), 9764864);
    }

    private void specialAttack()
    {
        client.invokeMenuAction("Special Attack", "", 1, 5, EquipmentInventorySlot.WEAPON.getSlotIdx(), 9764864);
    }
}

// DhVengPkingConfig.java

package com.yourname.dhvengpking;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;

@ConfigGroup("dhvengpking")
public interface DhVengPkingConfig extends Config
{
}

// build.gradle

plugins {
    id 'java'
}

group 'com.yourname'
version '1.0'

repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'net.runelite:client:1.8.14'
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}
