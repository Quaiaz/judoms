package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.Servers;
import constants.WorldConstants.TespiaServers;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.PingTimer;
import server.Timer.WorldTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.MapleAESOFB;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);

    public void run() throws InterruptedException {

        if (Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin")) || ServerConstants.Use_Localhost) {
            System.out.println("Maintenance is currently active.");
        }

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("Runtime Exception - Check if the SQL Database is connected.");
        }

        System.out.println("JudoMS is Active.");
        World.init();
        if (Boolean.parseBoolean(ServerProperties.getProperty("logpackets"))) {
            System.out.println("Logging Packets.");
        }
        System.out.println("Maple Version: " + ServerConstants.MAPLE_VERSION + "." + ServerConstants.MAPLE_PATCH);
        System.out.println("Server Rates: " + WorldConstants.Servers.Scania.getExp() + "/" + WorldConstants.Servers.Scania.getMeso() + "/" + WorldConstants.Servers.Scania.getDrop());

        boolean encryptionfound = false;
        for (MapleAESOFB.EncryptionKey encryptkey : MapleAESOFB.EncryptionKey.values()) {
            if (("V" + ServerConstants.MAPLE_VERSION).equals(encryptkey.name())) {
                System.out.println("System has successfully found the packet encryption.");
                encryptionfound = true;
                break;
            }
        }
        if (!encryptionfound) {
            System.out.println("System could not locate encryption for the current version, so it is using the latest Encryption.");
        }
        System.out.println("Loading Instances...");
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll();
        MapleFamily.loadAll();
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        MapleItemInformationProvider.getInstance().runItems();
        SkillFactory.load();
        LoginInformationProvider.getInstance();
        RandomRewards.load();
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        CharacterCardFactory.getInstance().initialize();
        MobSkillFactory.getInstance();
        SpeedRunner.loadSpeedRuns();
        MTSStorage.load();
        MapleInventoryIdentifier.getInstance();
        MapleMapFactory.loadCustomLife();
        System.out.println("Finished Loading Instances");
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("DELETE FROM `moonlightachievements` where achievementid > 0;");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        }
        CashItemFactory.getInstance().initialize();
        MapleServerHandler.initiate();
        System.out.println("Loading JudoMS Login Server...");
        LoginServer.run_startup_configurations();
        System.out.println("JudoMS Login Server is Online!");

        System.out.println("Loading JudoMS Channel Server...");
        ChannelServer.startChannel_Main();
        System.out.println("JudoMS Channel Server is Online!");

        System.out.println("Loading JudoMS Cash Shop Server...");
        CashShopServer.run_startup_configurations();
        System.out.println("JudoMS Cash Shop Server is Online!");
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        System.out.println("Channel Respawn Worker has been registered.");
        ShutdownServer.registerMBean();
        PlayerNPC.loadAll();
        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.setOn();
        RankingWorker.run();
        System.out.println("JudoMS Server has launched successfully in " + ((System.currentTimeMillis() - startTime) / 1000) + " Seconds.");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }
}