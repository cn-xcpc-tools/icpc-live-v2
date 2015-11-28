package ru.ifmo.acm.mainscreen.statuses;

import ru.ifmo.acm.events.ContestInfo;
import ru.ifmo.acm.events.EventsLoader;
import ru.ifmo.acm.events.PCMS.PCMSEventsLoader;
import ru.ifmo.acm.events.TeamInfo;

import java.util.Arrays;

public class TeamStatus {
    public final ContestInfo info;
    public final String[] teamNames;

    public TeamStatus() {
        EventsLoader loader = PCMSEventsLoader.getInstance();
        info = loader.getContestData();
        TeamInfo[] teamInfos = info.getStandings();
        teamNames = new String[teamInfos.length];
        for (int i = 0; i < teamNames.length; i++) {
            teamNames[i] = teamInfos[i].getName();
        }
        Arrays.sort(teamNames);
    }

    public void recache() {
        //Data.cache.refresh(TeamData.class);
    }

    public synchronized boolean setInfoVisible(boolean visible, String type, String teamName) {
        if (visible && isInfoVisible) {
            return false;
        }
        infoTimestamp = System.currentTimeMillis();
        isInfoVisible = visible;
        infoType = type;
        infoTeam = info.getParticipant(teamName);
        return true;
    }

    public synchronized String infoStatus() {
        return infoTimestamp + "\n" + isInfoVisible + "\n" + infoType + "\n" + (infoTeam == null ? null : infoTeam.getName());
    }

    private long infoTimestamp;
    private boolean isInfoVisible;
    private String infoType;
    private TeamInfo infoTeam;
}