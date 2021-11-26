package me.rochblondiaux.network.packets.server.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.ServerPacketIdentifier;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@NoArgsConstructor
public class LoginSuccessPacket implements ServerPacket {

    @Getter
    @Setter
    private UUID uniqueId;

    public LoginSuccessPacket(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeUuid(uniqueId);
    }

    @Override
    public void read(BinaryReader reader) {
        uniqueId = reader.readUuid();
    }

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.LOGIN_SUCCESS;
    }
}
