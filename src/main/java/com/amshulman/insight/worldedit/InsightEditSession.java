package com.amshulman.insight.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bags.BlockBag;

final class InsightEditSession extends EditSession {

    public InsightEditSession(LocalWorld world, int maxBlocks) {
        super(world, maxBlocks);
    }

    public InsightEditSession(LocalWorld world, int maxBlocks, BlockBag blockBag) {
        super(world, maxBlocks, blockBag);
    }
}
