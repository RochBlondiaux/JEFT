package me.rochblondiaux.network.packets.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rochblondiaux.commons.utils.binary.BinaryReader;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;
import me.rochblondiaux.network.packets.server.ServerPacket;
import me.rochblondiaux.network.packets.server.ServerPacketIdentifier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisconnectPacket implements ServerPacket {

    @Getter
    private String reason;

    @Override
    public ServerPacketIdentifier getIdentifier() {
        return ServerPacketIdentifier.DISCONNECT;
    }

    @Override
    public void write(BinaryWriter writer) {
        writer.writeSizedString(reason);
    }

    @Override
    public void read(BinaryReader reader) {
        this.reason = reader.readSizedString();
    }
}
