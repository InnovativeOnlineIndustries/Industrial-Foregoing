/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.proxy.network;

import com.buuz135.industrial.item.infinity.InfinityStackHolder;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.PlayerInventoryFinder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackOpenedMessage extends Message {

    private int slot;
    private String finder;

    public BackpackOpenedMessage(int slot, String finder) {
        this.slot = slot;
        this.finder = finder;
    }

    public BackpackOpenedMessage() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            InfinityStackHolder.TARGET = new PlayerInventoryFinder.Target(finder, PlayerInventoryFinder.get(finder).get(), slot);
        });
    }
}
