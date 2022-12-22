package cum.jesus.jesusclient.module.modules.skyblock;

import cum.jesus.jesusclient.events.DrawBackgroundEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TerminalSolver extends Module {
    private static NumberSetting<Long> clickDelay = new NumberSetting<>("Click delay", 60L, 5L, 100L);
    private static BooleanSetting noDelay = new BooleanSetting("No delay", false);

    private static final ArrayList<Slot> clickQueue = new ArrayList<>(28);
    private static final int[] mazeDirection = {-9, -1, 1, 9};
    private static TerminalType currentTerminal = TerminalType.NONE;
    private static int targetColorIndex = -1; // For all same color terminals
    private static long lastClickTime = 0;
    private static int windowId = 0;
    private static int windowClicks = 0;
    private static boolean recalculate = false;
    public static boolean testing = false;

    private enum TerminalType {
        MAZE, NUMBERS, CORRECT_ALL, LETTER, COLOR, NONE, TOGGLE_COLOR, TIMING
    }

    public TerminalSolver() {
        super("TerminalSolver", "Solves terminals", Category.SKYBLOCK);
    }

    @EventTarget
    public void guiDraw(DrawBackgroundEvent event) {
        if (!isToggled() || !SkyblockUtils.inDungeon) return;

        if (event.gui instanceof GuiChest) {
            Container container = ((GuiChest)event.gui).inventorySlots;
            if (container instanceof ContainerChest) {
                List<Slot> slots = container.inventorySlots;

                if (currentTerminal == TerminalType.NONE) {
                    String chestName = ((ContainerChest)container).getLowerChestInventory().getDisplayName().getUnformattedText();
                    Logger.debug("Chest name: " + chestName);

                    if(chestName.equals("Navigate the maze!")) {
                        currentTerminal = TerminalType.MAZE;
                    } else if(chestName.equals("Click in order!")) {
                        currentTerminal = TerminalType.NUMBERS;
                    } else if(chestName.equals("Correct all the panes!")) {
                        currentTerminal = TerminalType.CORRECT_ALL;
                    } else if(chestName.startsWith("What starts with: '")) {
                        currentTerminal = TerminalType.LETTER;
                    } else if(chestName.startsWith("Select all the")) {
                        currentTerminal = TerminalType.COLOR;
                    } else if(chestName.equals("Change all to same color!")) {
                        currentTerminal = TerminalType.TOGGLE_COLOR;
                    }
                } else {
                    if (clickQueue.isEmpty() || recalculate) {
                        recalculate = getClicks((ContainerChest)container);
                    } else {
                        switch(currentTerminal) {
                            case MAZE:
                            case NUMBERS:
                            case CORRECT_ALL:
                                clickQueue.removeIf(slot -> slots.get(slot.slotNumber).getHasStack() && slots.get(slot.slotNumber).getStack().getItemDamage() == 5);
                                break;
                            case LETTER:
                            case COLOR:
                                clickQueue.removeIf(slot -> slots.get(slot.slotNumber).getHasStack() && slots.get(slot.slotNumber).getStack().isItemEnchanted());
                                break;
                            case TOGGLE_COLOR:
                                clickQueue.removeIf(slot -> slots.get(slot.slotNumber).getHasStack() && slots.get(slot.slotNumber).getStack().getItemDamage() == targetColorIndex);
                                break;
                        }
                    }

                    if (!clickQueue.isEmpty()) {
                        if (isToggled() && System.currentTimeMillis() - lastClickTime > clickDelay.getObject()) {
                            clickSlot(clickQueue.get(0));
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!isToggled() || !SkyblockUtils.inDungeon) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (!(mc.currentScreen instanceof GuiChest)) {
            reset();
        }
    }

    private static void reset() {
        currentTerminal = TerminalType.NONE;
        clickQueue.clear();
        windowClicks = 0;
        targetColorIndex = -1;
    }

    private boolean getClicks(ContainerChest container) {
        List<Slot> invSlots = container.inventorySlots;
        String chestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
        clickQueue.clear();
        switch(currentTerminal) {
            case MAZE:
                int[] mazeDirection = new int[]{-9, -1, 1, 9};
                boolean[] isStartSlot = new boolean[54];
                int endSlot = -1;
                // Scan chest for start and end
                for(Slot slot : invSlots) {
                    if(slot.inventory == mc.thePlayer.inventory) continue;
                    ItemStack itemStack = slot.getStack();
                    if(itemStack == null) continue;
                    if(itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)) {
                        if(itemStack.getItemDamage() == 5) {
                            isStartSlot[slot.slotNumber] = true;
                        } else if(itemStack.getItemDamage() == 14) {
                            endSlot = slot.slotNumber;
                        }
                    }
                }
                // Plan route for maze from start to end
                for(int slot = 0; slot < 54; slot++) {
                    if(isStartSlot[slot]) {
                        boolean[] mazeVisited = new boolean[54];
                        int startSlot = slot;
                        while(startSlot != endSlot) {
                            boolean newSlotChosen = false;
                            for(int i : mazeDirection) {
                                int nextSlot = startSlot + i;
                                if(nextSlot < 0 || nextSlot > 53 || i == -1 && startSlot % 9 == 0 || i == 1 && startSlot % 9 == 8) continue;
                                if(nextSlot == endSlot) return false;
                                if(mazeVisited[nextSlot]) continue;
                                ItemStack itemStack = invSlots.get(nextSlot).getStack();
                                if(itemStack == null) continue;
                                if(itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane) && itemStack.getItemDamage() == 0) {
                                    clickQueue.add(invSlots.get(nextSlot));
                                    startSlot = nextSlot;
                                    mazeVisited[nextSlot] = true;
                                    newSlotChosen = true;
                                    break;
                                }
                            }
                            // Prevents infinite loop if there is no adjacent white pane
                            if(!newSlotChosen) {
                                Logger.warn("Maze calculation aborted");
                                return true;
                            }
                        }
                    }
                }
                return true;

            case NUMBERS:
                int min = 0;
                Slot[] temp = new Slot[14];
                for(int i = 10; i <= 25; i++) {
                    if(i == 17 || i == 18) continue;
                    ItemStack itemStack = invSlots.get(i).getStack();
                    if(itemStack == null) continue;
                    if(itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane) && itemStack.stackSize < 15) {
                        if(itemStack.getItemDamage() == 14) {
                            temp[itemStack.stackSize - 1] = invSlots.get(i);
                        } else if(itemStack.getItemDamage() == 5) {
                            if(min < itemStack.stackSize) {
                                min = itemStack.stackSize;
                            }
                        }
                    }
                }
                clickQueue.addAll(Arrays.stream(temp).filter(Objects::nonNull).collect(Collectors.toList()));
                if(clickQueue.size() != 14 - min) return true;
                break;

            case CORRECT_ALL:
                for(Slot slot : invSlots) {
                    if(slot.inventory == mc.thePlayer.inventory) continue;
                    if(slot.slotNumber < 9 || slot.slotNumber > 35 || slot.slotNumber % 9 <= 1 || slot.slotNumber % 9 >= 7)
                        continue;
                    ItemStack itemStack = slot.getStack();
                    if(itemStack == null) return true;
                    if(itemStack.getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane) && itemStack.getItemDamage() == 14) {
                        clickQueue.add(slot);
                    }
                }
                break;

            case LETTER:
                if(chestName.length() > chestName.indexOf("'") + 1) {
                    char letterNeeded = chestName.charAt(chestName.indexOf("'") + 1);
                    for(Slot slot : invSlots) {
                        if(slot.inventory == mc.thePlayer.inventory) continue;
                        if(slot.slotNumber < 9 || slot.slotNumber > 44 || slot.slotNumber % 9 == 0 || slot.slotNumber % 9 == 8)
                            continue;
                        ItemStack itemStack = slot.getStack();
                        if(itemStack == null) return true;
                        if(itemStack.isItemEnchanted()) continue;
                        if(StringUtils.stripControlCodes(itemStack.getDisplayName()).charAt(0) == letterNeeded) {
                            clickQueue.add(slot);
                        }
                    }
                }
                break;

            case COLOR:
                // Get color from chest name
                String colorNeeded = null;
                for(EnumDyeColor color : EnumDyeColor.values()) {
                    String colorName = color.getName().replaceAll("_", " ").toUpperCase();
                    if(chestName.contains(colorName)) {
                        colorNeeded = color.getUnlocalizedName();
                        break;
                    }
                }

                if(colorNeeded != null) {
                    for(Slot slot : invSlots) {
                        if(slot.inventory == mc.thePlayer.inventory) continue;
                        if(slot.slotNumber < 9 || slot.slotNumber > 44 || slot.slotNumber % 9 == 0 || slot.slotNumber % 9 == 8)
                            continue;
                        ItemStack itemStack = slot.getStack();
                        if(itemStack == null) return true;
                        if(itemStack.isItemEnchanted()) continue;
                        if(itemStack.getUnlocalizedName().contains(colorNeeded)) {
                            clickQueue.add(slot);
                        }
                    }
                }
                break;

            case TOGGLE_COLOR:
                for(Slot slot : invSlots) {
                    if(slot.inventory == mc.thePlayer.inventory) continue;
                    if(targetColorIndex == -1) targetColorIndex = getTargetColorIndex(mc.thePlayer.openContainer.inventorySlots);
                    ItemStack itemStack = slot.getStack();
                    if(itemStack == null) continue;
                    if(!colorOrder.contains(itemStack.getItemDamage())) continue;

                    boolean leftClick = (colorOrder.indexOf(targetColorIndex) - colorOrder.indexOf(itemStack.getItemDamage()) + colorOrder.size()) % colorOrder.size() < Math.round(colorOrder.size() / 2f);
                    if(leftClick) {
                        clickQueue.add(slot);
                    } else {
                        clickSlot(slot, 1, 0);
                    }
                }
                if(targetColorIndex != -1) {
                    // targetColorIndex = colorOrder.get((int) (totalWeight / 9f));
                    Logger.debug(targetColorIndex);
                }
                break;
        }
        return false;
    }

    // red orange yellow green blue
    private static final ArrayList<Integer> colorOrder = new ArrayList<>(Arrays.asList(14, 1, 4, 13, 11));
    private static int getTargetColorIndex(List<Slot> slots) {
        if(slots.isEmpty()) return 15;

        float sum = 0;
        for(Slot slot : slots) {
            if(slot == null) continue;
            ItemStack stack = slot.getStack();
            if(stack == null) continue;
            sum += colorOrder.indexOf(stack.getItemDamage());
        }

        int index = Math.round(sum / slots.size());
        return colorOrder.size() > index ? index : -1;
    }

    private void clickSlot(Slot slot) {
        if(testing) {
            clickSlot(slot, 0, 1);
        } else {
            clickSlot(slot, 2, 0);
        }
    }

    private void clickSlot(Slot slot, int clickButton, int clickMode) {
        if(windowClicks == 0) windowId = mc.thePlayer.openContainer.windowId;
        mc.playerController.windowClick(windowId + windowClicks, slot.slotNumber, clickButton, clickMode, mc.thePlayer);
        lastClickTime = System.currentTimeMillis();
        // Immediately remove from queue before gui updates
        if(noDelay.getObject()) {
            windowClicks++;
            clickQueue.remove(slot);
        }
    }
}
