package com.amshulman.insight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.block.BlockFace;

import com.google.common.collect.ObjectArrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static final BlockFace[] CARDINAL_DIRECTIONS = new BlockFace[] { BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH };
    public static final BlockFace[] ALL_FLOW_DIRECTIONS = ObjectArrays.concat(CARDINAL_DIRECTIONS, BlockFace.DOWN);

}
