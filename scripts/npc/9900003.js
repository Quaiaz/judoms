//By Blake
//Starting Npc - Sera
function start() {
    cm.sendSimple("Hello #b#h ##k,\r\n\r\nWelcome to #dJudoMS#k. Here's the list of information about #dJudoMS#k if you haven't know:\r\n\r\n#gRates: 512x / 1x / 1x\r\n\Version: 117.2\r\nRevision: 0.0.1\r\nAdministrator: Judo#k\r\n\r\nBy playing JudoMS, you agree #rNOT#k to break any of the following rules:\r\n\r\n#rAdvertising\r\nSpamming\r\nTrolling\r\nDisrespectful\r\nFlaming\r\nMisuse\r\nRacism\r\nHacking#k\r\n\r\nIf you have been found that you do not follow the following rules that are stated above, you will be given a punishment.\r\n\r\nThank you for taking your time to read the information and rules.\r\n\r\nUse the command @help to view a list of commands that are available.\r\n\r\n#g#L0#I have read the following information and I AGREE to abide the rules.#k");
}

function action(mode, type, selection) {
    cm.dispose();
    if (selection == 0) {
        cm.gainItem(1002419, 1); // Mark of the Beta
        cm.gainItem(2000005, 300); // Power Elixir
        cm.gainItem(5450000, 1); // Miu Miu
        cm.warp(800000000, 3);
        // cm.maxAllSkills();
        cm.sendOk("Enjoy your Starter Pack.");
        cm.dispose();
    }
}