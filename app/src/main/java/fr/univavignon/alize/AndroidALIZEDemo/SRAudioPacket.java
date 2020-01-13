package fr.univavignon.alize.AndroidALIZEDemo;

public class SRAudioPacket {
    private short[] audioPacket;
    private String text;

    public SRAudioPacket(short[] audioPacket, String text) {
        this.audioPacket = audioPacket;
        this.text = text;
    }

    public boolean packetHasText() {
        return !text.isEmpty();
    }

    public short[] getAudioPacket() {
        return audioPacket;
    }
}
