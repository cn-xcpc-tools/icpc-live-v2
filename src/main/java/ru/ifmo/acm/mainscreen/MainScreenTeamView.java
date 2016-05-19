package ru.ifmo.acm.mainscreen;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import ru.ifmo.acm.backend.player.urls.TeamUrls;
import ru.ifmo.acm.events.TeamInfo;

import java.util.Set;

/**
 * Created by Aksenov239 on 21.11.2015.
 */
public class MainScreenTeamView extends CustomComponent implements View {
    public static String NAME = "mainscreen-team";

    Label teamStatus;
    Button teamShow;
    Button teamHide;
    final String AUTOMATIC_STOPPED_STATUS = "Not automated<br><br>";
    Label automaticStatus;
    Button automatedShow;
    Button automatedStop;
    ComboBox automatedNumber;
    final String[] types = TeamUrls.types;
    ComboBox type;
    //    ListSelect teamSelection;
    OptionGroup teamSelection;
    CheckBox stats;


    public static String getTeamStatus() {
        String status = MainScreenData.getMainScreenData().teamData.infoStatus();
        return Utils.getTeamStatus(status);
//        String[] z = status.split("\n");
//
//        if (z[1].equals("true")) {
//            for (String type1 : TeamWidget.types) {
//                if (type1.equals(z[2])) {
//                    return "Now showing " + z[2] + " of team " + z[3] + " for " + (System.currentTimeMillis() - Long.parseLong(z[0])) / 1000 + " seconds";
//                }
//            }
//            return "Some error happened";
//        } else {
//            return "No team view is shown";
//        }
    }

    public Component getControllerTeam() {
        teamStatus = new Label(getTeamStatus());

        type = new ComboBox();
        type.addItems(types);
        type.setNullSelectionAllowed(false);
        type.setValue(types[0]);

        type.addValueChangeListener(event -> {
            if (teamSelection.getValue() == null)
                return;
            if (mainScreenData.teamData.isVisible() &&
                    !mainScreenData.teamData.setInfoManual(true, (String) type.getValue(), (TeamInfo) teamSelection.getValue())) {
                type.setValue(mainScreenData.teamData.infoType);
                Notification.show("You need to wait " + MainScreenData.getProperties().sleepTime + " seconds first", Type.WARNING_MESSAGE);
            } else {
                mainScreenData.teamStatsData.setVisible(stats.getValue(), (TeamInfo) teamSelection.getValue());
            }
        });

        //teamSelection = new ListSelect();
        //teamSelection.addItems(mainScreenData.teamStatus.teamNames);
        //teamSelection.setNullSelectionAllowed(false);
        //teamSelection.setHeight("100%");
        //teamSelection.setSizeFull();
        //teamSelection.setRows(mainScreenData.teamStatus.teamNames.length);

        automaticStatus = new Label(AUTOMATIC_STOPPED_STATUS, ContentMode.HTML);
        automatedShow = new Button("Show top teams");
        automatedShow.addClickListener(event -> {
            if (mainScreenData.teamData.inAutomaticShow()) {
                Notification.show("Automatic show is already on", Type.WARNING_MESSAGE);
                return;
            }
            if (mainScreenData.teamData.automaticStart((int) automatedNumber.getValue())) {
                Notification.show(automatedNumber.getValue() + " first teams are in automatic show", Type.TRAY_NOTIFICATION);
            } else {
                Notification.show("You need to wait " + MainScreenData.getProperties().sleepTime + " seconds first", Type.WARNING_MESSAGE);
            }
        });
        automatedStop = new Button("Stop automatic");
        automatedStop.addClickListener(event -> {
            if (!mainScreenData.teamData.inAutomaticShow()) {
                return;
            }
            mainScreenData.teamData.automaticStop();
        });
        automatedNumber = new ComboBox();
        automatedNumber.addItems(3, 4, 5, 8, 10, 12, 15, 20);
        automatedNumber.setNullSelectionAllowed(false);
        automatedNumber.setValue(10);

        teamSelection = new OptionGroup();
        teamSelection.setHtmlContentAllowed(true);

        Set<Integer> topTeamsIds = MainScreenData.getProperties().topteamsids;
        for (TeamInfo team : MainScreenData.getProperties().teamInfos) {
            teamSelection.addItem(team);
            String teamHtml = topTeamsIds.contains(team.getId()) ? "<b>" + team.toString() + "</b>" : team.toString();
            teamSelection.setItemCaption(team, teamHtml);
//            log.debug(team.toString());
        }
        teamSelection.setValue(MainScreenData.getProperties().teamInfos[0]);
        teamSelection.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        teamSelection.setWidth("100%");

        teamSelection.addValueChangeListener(event -> {
            if (mainScreenData.teamData.isVisible()) {
                if (mainScreenData.teamData.inAutomaticShow()) {
                    Notification.show("You need to stop automatic show first", Type.WARNING_MESSAGE);
                    return;
                }
                if (!mainScreenData.teamData.setInfoManual(true, (String) type.getValue(), (TeamInfo) teamSelection.getValue())) {
                    teamSelection.setValue(mainScreenData.teamData.getTeamString());
                    Notification.show("You need to wait " + MainScreenData.getProperties().sleepTime + " seconds first", Type.WARNING_MESSAGE);

                } else {
                    mainScreenData.teamStatsData.setVisible(stats.getValue(), (TeamInfo) teamSelection.getValue());
                }
            }
        });

        stats = new CheckBox("Statistics");

        teamShow = new Button("Show info");
        teamShow.addClickListener(event -> {
            if (mainScreenData.teamData.inAutomaticShow()) {
                Notification.show("You need to stop automatic show first", Type.WARNING_MESSAGE);
                return;
            }
            if (!mainScreenData.teamData.setInfoManual(true, (String) type.getValue(), (TeamInfo) teamSelection.getValue())) {
                Notification.show("You need to wait " + MainScreenData.getProperties().sleepTime + " seconds first", Type.WARNING_MESSAGE);
            } else {
                mainScreenData.teamStatsData.setVisible(stats.getValue(), (TeamInfo) teamSelection.getValue());
            }
        });

        teamHide = new Button("Hide info");
        teamHide.addClickListener(event -> {
            if (mainScreenData.teamData.inAutomaticShow()) {
                Notification.show("You need to stop automatic show first");
                return;
            }
            mainScreenData.teamData.setInfoManual(false, null, null);
            mainScreenData.teamStatsData.setVisible(false, null);
        });

        Component controlAutomaticGroup = createGroupLayout(automatedShow, automatedStop, automatedNumber);
        Component controlGroup = createGroupLayout(stats, teamShow, teamHide, type);
        VerticalLayout result = new VerticalLayout(
                automaticStatus,
                controlAutomaticGroup,
                teamStatus,
                controlGroup,
                teamSelection
        );
        result.setSpacing(true);
        result.setSizeFull();
        result.setHeight("100%");
        result.setComponentAlignment(controlAutomaticGroup, Alignment.MIDDLE_CENTER);
        result.setComponentAlignment(teamStatus, Alignment.MIDDLE_CENTER);
        result.setComponentAlignment(controlGroup, Alignment.MIDDLE_CENTER);

        return result;
    }

    /* Main screen */
    MainScreenData mainScreenData;

    public MainScreenTeamView() {
        mainScreenData = MainScreenData.getMainScreenData();

        Component main = getControllerTeam();
        main.setSizeFull();
        main.setHeight("100%");

        setCompositionRoot(main);
    }

    public void refresh() {
        teamStatus.setValue(getTeamStatus());
        String automatic = mainScreenData.teamData.automaticStatus();
        automaticStatus.setValue(automatic.length() == 0 ? AUTOMATIC_STOPPED_STATUS : automatic);
//        mainScreenData.update();
    }

    public void enter(ViewChangeEvent event) {

    }


    private CssLayout createGroupLayout(Component... components) {
        CssLayout layout = new CssLayout();
        layout.addStyleName("v-component-group");
        layout.addComponents(components);

        return layout;
    }
}
