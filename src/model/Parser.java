package model;

import model.ircevent.*;

public class Parser {
	/**
	 * Parse raw string from server
	 * @param raw raw string to parse
	 * @return IRCEvent generated by parser
	 */
	public static IRCEvent parse(String raw)
	{
		String r = raw.substring(1);
		int split = r.indexOf(":");
		String data = null;
		String []meta = null;
		if (split  + 1 < r.length())
			data = r.substring(split + 1);
		if (split != -1)
			meta = r.substring(0, split).split(" ");
		else
			meta = r.split(" ");
		if (meta[0].contains("@"))
		{
			if (meta[1].equals("PRIVMSG"))
			{
				String user = meta[0].split("!")[0];
				String channel = meta[2];
				return new PrivmsgEvent(channel, data, user);
			}
			else if (meta[1].equals("JOIN"))
			{
				String user = meta[0].split("!")[0];
				String channel = meta[2];
				return new JoinEvent(channel,user);
			}
			else if (meta[1].equals("PART"))
			{
				String user = meta[0].split("!")[0];
				String channel = meta[2];
				return new PartEvent(channel, user);
			}
//	:asdasdasd_!webchat@user-109-243-23-184.play-internet.pl TOPIC #e-sim.bt :Simple Topic
			else if (meta[1].equals("TOPIC"))
			{
				String user = meta[0].split("!")[0];
				String channel = meta[2];
				return new TopicChangeEvent(user, channel, data);
			}
		}
		//:underworld1.no.quakenet.org 353 ArP = #Nad_Romantycznym_Ruczajem :ArP @Ukar Focuus Bzzzyk DmoszkuGTX Brenia Mrowa CzarodziejKamil FrankZappa Randomowiec Slannesh BratPL chinczyk666 Vasu_ FxK Opi RanchoCucamonga ISO9001 PieknyRoman Bachu MrAlpaka cbool22 +RmX Kurczaki +Jaa Rev| Ender4K TechnoTampon +Maverick91 @CyfrowyDante kasper93 xnt chrom64 kobitka panredaktor ImQ009 Silwanos Miksuss Kamazjest +Sesus_exe Acardul ast @negocki Radio-Erewan_ @B4rt0[off] +Normalny[w] Dexior BOT_Poorchat
		else
		{
			if (meta[1].equals("353"))//names event
			{
				String channel = meta[4];
				String n[] = data.split(" ");
				NamesEvent ne = new NamesEvent(channel);
				for (int i = 0; i < n.length; i++)
					ne.getNicks().add(new User(n[i]));
				return ne;
			}
//	:servercentral.il.us.quakenet.org 332 ArP #e-sim.bt :Simple Topic
			else if (meta[1].equals("332"))
			{
				String channel = meta[3];
				return new TopicEvent(channel, data);
			}
		}
		return new RAWEvent(raw);
	}
}
