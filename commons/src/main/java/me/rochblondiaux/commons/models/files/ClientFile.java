package me.rochblondiaux.commons.models.files;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Data
@RequiredArgsConstructor
public class ClientFile {

    private final UUID uniqueId;
    private final InetAddress address;
    private final File file;
    private final long size;
    private final Date uploadDate;

    public ClientFile(InetAddress address, File file, long size) {
        this.uniqueId = UUID.randomUUID();
        this.address = address;
        this.file = file;
        this.size = size;
        this.uploadDate = new Date();
    }

    public FileInformation toFileInformation() {
        return new FileInformation(uniqueId, file.getName(), size, uploadDate);
    }
}
