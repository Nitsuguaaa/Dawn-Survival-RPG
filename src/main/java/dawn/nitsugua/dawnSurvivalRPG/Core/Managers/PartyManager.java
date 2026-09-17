package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Parties.Party;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dr = DawnRegistry.getInstance();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Long>> inviteList = new ConcurrentHashMap<>();
    private final long INVITE_DURATION = 60000; //a minute?

    public PartyManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void createParty(Player player, String partyName) {
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        if(dp == null) return;

        if(dp.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're already in a party!"));
            return;
        }
        Party party = new Party(partyName, player.getUniqueId());
        party.addMember(player.getUniqueId(), Party.Role.LEADER);
        dr.addParty(party);
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Registered party <white>" + partyName + "<green>!"));
    }

    public void disbandParty(Player player) {
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        if(dp == null || !dp.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
            return;
        }

        UUID partyId = dp.getPartyId();
        Party party = dr.getParty(partyId);

        if(!player.getUniqueId().equals(party.getLeader())) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not the leader!"));
            return;
        }

        for(UUID member : party.getMembers().keySet()) {
            party.removeMember(member);
        }

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "Party <white>" + party.getPartyName() + "<green> has been disbanded!"));

        // Clean up all invites FROM this party across all players
        inviteList.values().forEach(playerInvites ->
                playerInvites.remove(partyId)
        );

        // Remove empty entries to prevent memory leaks
        inviteList.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        dr.removeParty(partyId);
    }

    public void addMember(Player player, String memberName) {
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        //if player is not in a party
        if(dp == null || !dp.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
            return;
        }

        UUID partyId = dp.getPartyId();
        Party party = dr.getParty(partyId);

        //if player is not the leader of that party then return
        if(!player.getUniqueId().equals(party.getLeader())) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not the leader!"));
            return;
        }

        UUID memberId = Bukkit.getPlayerUniqueId(memberName);
        if (memberId == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player not found!"));
            return;
        }

        DawnPlayer member = dr.getDawnPlayer(memberId);
        if (member == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player data not loaded!"));
            return;
        }

        if(member.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player is already in a party!"));
            return;
        }

        party.addMember(memberId, Party.Role.MEMBER);
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "<white>" + memberName + "<green> has been added to the party!"));

        // Notify the member if they're online
        Player memberPlayer = Bukkit.getPlayer(memberId);
        if (memberPlayer != null) {
            memberPlayer.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                    "You have joined <white>" + party.getPartyName() + "<green>!"));
        }
    }

    public void removeMember(Player player, String memberName) {
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        //if player is not in a party
        if(dp == null || !dp.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
            return;
        }

        UUID partyId = dp.getPartyId();
        Party party = dr.getParty(partyId);

        //if player is not the leader of that party then return
        if(!player.getUniqueId().equals(party.getLeader())) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not the leader!"));
            return;
        }

        UUID memberId = Bukkit.getPlayerUniqueId(memberName);
        if (memberId == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player not found!"));
            return;
        }

        // EDGE CASE 1: Prevent self-kicking
        if (player.getUniqueId().equals(memberId)) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You cannot kick yourself! Use /party leave or /party disband."));
            return;
        }

        DawnPlayer member = dr.getDawnPlayer(memberId);
        if (member == null || !member.isInParty() || !member.getPartyId().equals(partyId)) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Player data not loaded!"));
            return;
        }

        party.removeMember(memberId);
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "<white>" + memberName + "<green> has been kicked out of the party!"));

        // Notify the member if they're online
        Player memberPlayer = Bukkit.getPlayer(memberId);
        if (memberPlayer != null) {
            memberPlayer.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE,
                    "You have been kicked from <white>" + party.getPartyName() + "<red>!"));
        }

        broadcastToParty(party, "<white>" + memberName + "<gray> has been kicked from the party.");
    }

    public void leaveParty(Player player) {
        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
        //if player is not in a party
        if(dp == null || !dp.isInParty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
            return;
        }

        UUID partyId = dp.getPartyId();
        Party party = dr.getParty(partyId);
        UUID playerId = player.getUniqueId();

        if (party.getMembers().size() <= 1) {
            disbandParty(player); // Reuse your existing disband logic!
            return;
        }

        // EDGE CASE 3: Are they the leader? If so, transfer leadership first.
        if (playerId.equals(party.getLeader())) {
            // Find the first available member to promote
            for (UUID memberId : party.getMembers().keySet()) {
                if (!memberId.equals(playerId)) {
                    party.setLeader(memberId);

                    Player newLeader = Bukkit.getPlayer(memberId);
                    if (newLeader != null) {
                        newLeader.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                                "You are now the leader of <white>" + party.getPartyName() + "<green>!"));
                        broadcastToParty(party, "<white>" + newLeader.getName() + "<gray> is now the leader of the party.");
                    }
                    break;
                }
            }
        }

        party.removeMember(dp.getPlayerId());
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "you left the party!"));

        // Notify the rest of the party
        broadcastToParty(party, "<white>" + player.getName() + "<gray> has left the party.");
    }

    public void invitePlayer(Player inviter, Player invited) {
        DawnPlayer dp1 = dr.getDawnPlayer(inviter.getUniqueId());
        DawnPlayer dp2 = dr.getDawnPlayer(invited.getUniqueId());

        if(dp1 == null || !dp1.isInParty()) {
            inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
            return;
        }

        if (dp2 == null || dp2.isInParty()) {
            inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Cannot invite player to party!"));
            return;
        }

        // Optional: Check if inviter is the leader (if you only want leaders to invite)
        Party party = dr.getParty(dp1.getPartyId());
        if (!inviter.getUniqueId().equals(party.getLeader())) {
            inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Only the party leader can invite players!"));
            return;
        }

        // 2. FIX THE NPE: Use computeIfAbsent to safely get or create the inner map
        ConcurrentHashMap<UUID, Long> playerInviteList = inviteList.computeIfAbsent(
                invited.getUniqueId(),
                k -> new ConcurrentHashMap<>()
        );

        // 3. FIX THE MESSAGE: Clarify who has the pending invite
        if (playerInviteList.containsKey(party.getId())) {
            inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                    "<white>" + invited.getName() + "<red> already has a pending invite from your party!"));
            return;
        }

        // Add the invite (Expires in 60 seconds = 60000ms)
        long expirationTime = System.currentTimeMillis() + INVITE_DURATION;
        playerInviteList.put(party.getId(), expirationTime);

        inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "Sent a party invite to <white>" + invited.getName() + "<green>!"));

        // Notify the invited player
        invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE,
                "<white>" + inviter.getName() + "<gray> invited you to join <white>" + party.getPartyName() + "<gray>!"));

        // You can add clickable text here for /party accept [inviter_name]
    }

    public void acceptInvite(Player invited, Player inviter) {
        DawnPlayer dp1 = dr.getDawnPlayer(inviter.getUniqueId());
        DawnPlayer dp2 = dr.getDawnPlayer(invited.getUniqueId());

        if (dp1 == null || dp2 == null || !dp1.isInParty() || dp2.isInParty()) {
            invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Invite is invalid or you are already in a party!"));
            return;
        }

        Party party = dr.getParty(dp1.getPartyId());
        ConcurrentHashMap<UUID, Long> playerInviteList = inviteList.get(invited.getUniqueId());

        // 1. Check if invite exists
        if (playerInviteList == null || !playerInviteList.containsKey(party.getId())) {
            invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You don't have an invite from this party!"));
            return;
        }

        // 2. Check if invite is expired
        long expirationTime = playerInviteList.get(party.getId());
        if (System.currentTimeMillis() > expirationTime) {
            invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "This invite has expired!"));
            playerInviteList.remove(party.getId()); // Clean it up
            return;
        }

        // 3. SUCCESS! Add them to the party
        party.addMember(invited.getUniqueId(), Party.Role.MEMBER);

        // NEW (nuke it all - they're in a party now, nothing else matters):
        inviteList.remove(invited.getUniqueId());

        invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS,
                "You joined <white>" + party.getPartyName() + "<green>!"));

        broadcastToParty(party, "<white>" + invited.getName() + "<gray> has joined the party!");
    }

    public void declineInvite(Player invited, Player inviter) {
        DawnPlayer inviterDP = dr.getDawnPlayer(inviter.getUniqueId());

        // If the inviter isn't in a party anymore, the invite is invalid anyway
        if (inviterDP == null || !inviterDP.isInParty()) {
            invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "That party no longer exists!"));
            return;
        }

        Party party = dr.getParty(inviterDP.getPartyId());
        ConcurrentHashMap<UUID, Long> playerInviteList = inviteList.get(invited.getUniqueId());

        if (playerInviteList == null || !playerInviteList.containsKey(party.getId())) {
            invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You don't have an invite from this player!"));
            return;
        }

        // Remove the specific invite
        playerInviteList.remove(party.getId());
        if (playerInviteList.isEmpty()) {
            inviteList.remove(invited.getUniqueId());
        }

        invited.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Invite declined."));

        inviter.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE,
                "<white>" + invited.getName() + "<red> declined your party invite."));
    }

    public void broadcastToParty(Party party, String message) {
        for (UUID memberId : party.getMembers().keySet()) {
            Player onlineMember = Bukkit.getPlayer(memberId);
            if (onlineMember != null) {
                // We use MESSAGE type here so it doesn't conflict with SUCCESS/FAILURE colors
                onlineMember.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, message));
            }
        }
    }

    public boolean isTheSameParty(Player player1, Player player2) {
        DawnPlayer dp1 = dr.getDawnPlayer(player1.getUniqueId());
        DawnPlayer dp2 = dr.getDawnPlayer(player2.getUniqueId());
        if(dp1 == null || dp2 == null || !dp1.isInParty() || !dp2.isInParty()) return false;

        return dp1.getPartyId().equals(dp2.getPartyId());
    }













    //TODO: FOR FUTURE LEAVE FOR NOW
//    public void promoteMember(Player player, String memberName) {
//        DawnPlayer dp = dr.getDawnPlayer(player.getUniqueId());
//        if(dp == null) return;
//
//        if(!dp.isInParty()) {
//            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not in a party!"));
//            return;
//        }
//
//        UUID partyId = dp.getPartyId();
//        Party party = dr.getParty(partyId);
//
//        if(dp.getPlayerId() != party.getLeader()) {
//            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You're not the leader!"));
//            return;
//        }
//    }
}
