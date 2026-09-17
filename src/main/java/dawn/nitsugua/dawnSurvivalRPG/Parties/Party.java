package dawn.nitsugua.dawnSurvivalRPG.Parties;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Party {
    private final UUID id;
    private final String partyName;
    private UUID leader;
    private final ConcurrentHashMap<UUID, Role> members = new ConcurrentHashMap<>();

    public enum Role {
        LEADER,
        //OFFICER,
        MEMBER
    }

    public Party(String partyName, UUID leader) {
        id = UUID.randomUUID();
        this.partyName = partyName;
        this.leader = leader;
        addMember(leader, Role.LEADER);
    }

    public void setLeader(UUID newleader) {
        UUID oldLeader = this.leader;
        this.leader = newleader;

        members.put(newleader, Role.LEADER);
        if(oldLeader != null && members.containsKey(oldLeader)) {
            members.put(oldLeader, Role.MEMBER);
        }
    }

    public void addMember(UUID member, Role role) {
        members.put(member, role);
        DawnPlayer dp = DawnRegistry.getInstance().getDawnPlayer(member);
        if(dp != null) {
            dp.setParty(this.id);
        }
    }
    public void removeMember(UUID member) {
        members.remove(member);
        DawnPlayer dp = DawnRegistry.getInstance().getDawnPlayer(member);
        if(dp != null) {
            dp.removeParty();
        }
    }


    public UUID getId() {return id;}
    public String getPartyName() {return partyName;}
    public Map<UUID, Role> getMembers() {return Collections.unmodifiableMap(members);}
    public boolean isMember(UUID member) {return members.get(member) != null;}
    public UUID getLeader() {return leader;}

    //TODO: FOR FUTURE LEAVE FOR NOW
//    public boolean promoteMember(UUID member) {
//        if(members.get(member) == Role.MEMBER) {
//            members.put(member, Role.OFFICER);
//            return true;
//        }
//        return false;
//    }
//
//    public boolean demoteOfficer(UUID officer) {
//        if(members.get(officer) == Role.OFFICER) {
//            members.put(officer, Role.MEMBER);
//            return true;
//        }
//        return false;
//    }
}
