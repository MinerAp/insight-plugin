package com.amshulman.insight.worldedit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bags.BlockBag;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InsightEditSessionFactory extends EditSessionFactory {

    public static void initialize() {
        try {
            Class<EditSessionFactory> clazz = com.sk89q.worldedit.EditSessionFactory.class;
            clazz.getDeclaredMethod("getEditSession", LocalWorld.class, int.class, BlockBag.class, LocalPlayer.class);
            WorldEdit.getInstance().setEditSessionFactory(new InsightEditSessionFactory());
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public EditSession getEditSession(LocalWorld world, int maxBlocks) {
        return new InsightEditSession(world, maxBlocks);
    }

    @Override
    public EditSession getEditSession(LocalWorld world, int maxBlocks, BlockBag blockBag) {
        return new InsightEditSession(world, maxBlocks, blockBag);
    }
}
