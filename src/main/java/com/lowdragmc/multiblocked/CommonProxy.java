package com.lowdragmc.multiblocked;

import com.lowdragmc.multiblocked.api.definition.ControllerDefinition;
import com.lowdragmc.multiblocked.api.definition.PartDefinition;
import com.lowdragmc.multiblocked.api.pattern.JsonBlockPattern;
import com.lowdragmc.multiblocked.api.recipe.RecipeMap;
import com.lowdragmc.multiblocked.api.registry.MbdCapabilities;
import com.lowdragmc.multiblocked.api.registry.MbdComponents;
import com.lowdragmc.multiblocked.api.registry.MbdPredicates;
import com.lowdragmc.multiblocked.api.registry.MbdRenderers;
import com.lowdragmc.multiblocked.api.tile.BlueprintTableTileEntity;
import com.lowdragmc.multiblocked.network.MultiblockedNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;

public class CommonProxy {
    public CommonProxy() {
        MultiblockedNetworking.init();
        MbdCapabilities.registerCapabilities();
        MbdRenderers.registerRenderers();
        MbdPredicates.registerPredicates();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(CommonProxy.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registerComponents();
        IForgeRegistry<Block> registry = event.getRegistry();
        MbdComponents.registerBlocks(registry);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        MbdComponents.registerTileEntity(registry);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        MbdComponents.COMPONENT_ITEMS_REGISTRY.values().forEach(registry::register);
//        MbdItems.registerItems(registry);
    }

    public static void registerComponents(){
        // register any capability block
        MbdCapabilities.registerAnyCapabilityBlocks();
        // register blueprint table
        BlueprintTableTileEntity.registerBlueprintTable();
        // register controller tester
//        ControllerTileTesterEntity.registerTestController();
        // register part tester
//        PartTileTesterEntity.registerTestPart();
        // register JsonBlockPatternBlock
//        JsonBlockPatternWidget.registerBlock();
        // register JsonFiles
        MbdComponents.registerComponentFromFile(
                Multiblocked.GSON,
                new File(Multiblocked.location, "definition/controller"),
                ControllerDefinition.class,
                (definition, config) -> {
                    definition.basePattern = Multiblocked.GSON.fromJson(config.get("basePattern"), JsonBlockPattern.class).build();
                    definition.recipeMap = RecipeMap.RECIPE_MAP_REGISTRY.getOrDefault(config.get("recipeMap").getAsString(), RecipeMap.EMPTY);
                });
        MbdComponents.registerComponentFromFile(
                Multiblocked.GSON,
                new File(Multiblocked.location, "definition/part"),
                PartDefinition.class, null);
    }
}
