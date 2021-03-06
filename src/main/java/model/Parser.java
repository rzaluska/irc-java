package model;

import model.User.User;
import model.ircevent.*;

public class Parser {
    /**
     * Parse raw string from server
     *
     * @param raw raw string to parse
     * @return IRCEvent generated by parser
     */
    public static IRCEvent parse(String raw) {
        String rawString = raw.substring(1);
        int split = rawString.indexOf(":");
        String data = null;
        String[] meta;
        if (split + 1 < rawString.length())
            data = rawString.substring(split + 1);
        if (split != -1)
            meta = rawString.substring(0, split).split(" ");
        else
            meta = rawString.split(" ");
        if (meta[0].contains("@")) {
            if (meta[1].equals("PRIVMSG")) {
                String user = meta[0].split("!")[0];
                String channel = meta[2];
                if (data.getBytes()[0] == 1) {
                    return new IRCActionEvent(channel, data.substring(8,
                            data.length() - 1), user);
                } else
                    return new PrivmsgEvent(channel, data, user);
            } else if (meta[1].equals("JOIN")) {
                String user = meta[0].split("!")[0];
                if (data != null)
                    return new JoinEvent(data, user);
                String channel = meta[2];
                return new JoinEvent(channel, user);
            } else if (meta[1].equals("PART")) {
                String user = meta[0].split("!")[0];
                String channel = meta[2];
                return new PartEvent(channel, user);
            }
            // :asdasdasd_!webchat@user-109-243-23-184.play-internet.pl TOPIC
            // #e-sim.bt :Simple Topic
            else if (meta[1].equals("TOPIC")) {
                String user = meta[0].split("!")[0];
                String channel = meta[2];
                return new TopicChangeEvent(user, channel, data);
            } else if (meta[1].equals("MODE")) {
                if (data == null) {
                    String user = meta[0].split("!")[0];
                    String channel = meta[2];
                    String new_mode = meta[3];
                    String affectedUser = null;
                    if (meta.length == 5)
                        affectedUser = meta[4];
                    return new ModeEvent(user, channel, affectedUser, new_mode);
                } else {
                    String user = meta[0].split("!")[0];
                    String affe = meta[2];
                    return new ModeEvent(user, null, affe, data);
                }
            }
            // :qdasdasdasd!webchat@user-164-127-116-240.play-internet.pl NICK
            // :adddddccdc
            else if (meta[1].equals("NICK")) {
                String user = meta[0].split("!")[0];
                return new NickEvent(user, data);
            }
            // :asdasdasd_!webchat@user-164-127-88-29.play-internet.pl MODE
            // #e-sim.bt +o ArP
        } else {
            if (meta[1].equals("353"))// names event
            {
                String channel = meta[4];
                String namesTable[] = data.split(" ");
                NamesEvent namesEvent = new NamesEvent(channel);
                for (int i = 0; i < namesTable.length; i++) {
                    User user = User.buildUserFromRawString(namesTable[i]);
                    namesEvent.getNicks().add(user);
                }
                return namesEvent;
            }
            // :servercentral.il.us.quakenet.org 332 ArP #e-sim.bt :Simple Topic
            else if (meta[1].equals("332")) {
                String channel = meta[3];
                return new TopicEvent(channel, data);
            } else if (meta[1].matches("00[0-5]") || meta[1].matches("25[1-5]")
                    || meta[1].matches("372"))
                return new ServerInfoEvent(data);
            else if (meta[1].matches("376") || meta[1].matches("221")
                    || meta[1].matches("366"))
                return null;
        }
        return new RAWEvent(raw);
    }
}
