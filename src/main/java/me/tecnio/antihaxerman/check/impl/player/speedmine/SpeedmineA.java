//import io.github.retrooper.packetevents.PacketEvents;
//import io.github.retrooper.packetevents.packetwrappers.play.in.armanimation.WrappedPacketInArmAnimation;
//import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
//import io.github.retrooper.packetevents.utils.player.ClientVersion;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.potion.PotionEffectType;
//
//@CheckInfo(name = "SpeedMine", type = "A", description = "Checks if player is mining too fast.")
//public class SpeedmineA extends Check {
//
//    boolean started, canDestroy;
//    double damage, destroySpeed, blockDura;
//    long breakTime;
//
//    public SpeedmineB(PlayerData data) {
//        super(data);
//    }
//
//    @Override
//    public void handle(Packet packet) {
//        if(packet.isBlockDig()) {
//            WrappedPacketInBlockDig wrapped = new WrappedPacketInBlockDig(packet.getRawPacket());
//            Block block = new Location(data.getPlayer().getWorld(), wrapped.getBlockPosition().getX(), wrapped.getBlockPosition().getY(), wrapped.getBlockPosition().getZ()).getBlock();
//
//            destroySpeed = data.getPlayer().getItemInHand() == null || data.getPlayer().getItemInHand().getType().equals(Material.AIR)
//                    ? 1.0
//                    : ReflectionsUtil.getDestroySpeed(block, data.getPlayer());
//            blockDura = ReflectionsUtil.getBlockDurability(block);
//
//            switch (wrapped.getDigType()) {
//                case START_DESTROY_BLOCK:
//
//                    canDestroy = ReflectionsUtil.canDestroyBlock(data.getPlayer(), block) || PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9);
//                    started = true;
//                    breakTime = System.currentTimeMillis();
//                    debug("started");
//                    break;
//                case STOP_DESTROY_BLOCK:
//
//                    double destroySpeed = this.destroySpeed;
//                    //Taken from vanilla code in 1.8 client.
//                    if (data.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
//                        destroySpeed *= 1.0 + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.FAST_DIGGING) + 1) * 0.2f;
//                    } else if (data.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
//                        float f1;
//
//                        switch (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SLOW_DIGGING)) {
//                            case 1:
//                                f1 = 0.3F;
//                                break;
//
//                            case 2:
//                                f1 = 0.09F;
//                                break;
//
//                            case 3:
//                                f1 = 0.0027F;
//                                break;
//
//                            case 4:
//                            default:
//                                f1 = 8.1E-4F;
//                        }
//
//                        destroySpeed *= f1;
//                    }
//
//                    if (!data.getPlayer().isOnGround()) {
//                        destroySpeed /= 5f;
//                    }
//
//                    if (data.getPlayer().getItemInHand() != null && data.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.DIG_SPEED)) {
//                        int i = data.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.DIG_SPEED);
//                        destroySpeed += (float) (i * i + 1);
//                    }
//
//                    //End 1.8 skid.
//
//                    double delta;
//                    if (Math.abs(delta = (1 / ((destroySpeed / blockDura) / (!canDestroy ? 100 : 30)) * 50) - MathUtil.elapsed(breakTime)) > 300) {
//                        fail("Damage: " + damage + " Delta: " + delta);
//                    }
//
//                    debug((1 / ((destroySpeed / blockDura) / (!canDestroy ? 100 : 30)) * 50) + ", " + delta + ", " + canDestroy);
//
//                    damage =  destroySpeed = 0;
//                    started = canDestroy = false;
//                    break;
//                case ABORT_DESTROY_BLOCK: {
//                    damage = destroySpeed = 0;
//                    started = canDestroy = false;
//                }
//            }
//        }
//        else if(packet.isArmAnimation()) {
//            WrappedPacketInArmAnimation wrapped = new WrappedPacketInArmAnimation(packet.getRawPacket());
//            if (started && wrapped.readBlockPosition(0) != null) {
//                damage += ((destroySpeed / blockDura) / (!canDestroy ? 100 : 30));
//                debug(damage + " damage. " + canDestroy);
//            }
//        }
//    }
//}
