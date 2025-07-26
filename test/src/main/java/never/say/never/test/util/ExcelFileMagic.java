package never.say.never.test.util;

import org.apache.poi.poifs.storage.HeaderBlockConstants;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-03
 * @see org.apache.poi.poifs.filesystem.FileMagic
 */
public enum ExcelFileMagic {
    /**
     * CSV
     */
    CSV(-17, -69, -65),

    /**
     * XSL
     */
    XSL(HeaderBlockConstants._signature),

    /**
     * XLSX or XLSB
     */
    XLSX_XLSB(0x50, 0x4b, 0x03, 0x04),

    /**
     * UNKNOWN
     */
    UNKNOWN(new byte[0]);

    public static final int MAX_PATTERN_LENGTH = 8;

    final byte[][] magic;

    ExcelFileMagic(long magic) {
        this.magic = new byte[1][8];
        LittleEndian.putLong(this.magic[0], 0, magic);
    }

    ExcelFileMagic(int... magic) {
        byte[] one = new byte[magic.length];
        for (int i = 0; i < magic.length; i++) {
            one[i] = (byte) (magic[i] & 0xFF);
        }
        this.magic = new byte[][]{one};
    }

    ExcelFileMagic(byte[]... magic) {
        this.magic = magic;
    }

    ExcelFileMagic(String... magic) {
        this.magic = new byte[magic.length][];
        int i = 0;
        for (String s : magic) {
            this.magic[i++] = s.getBytes(LocaleUtil.CHARSET_1252);
        }
    }

    public static ExcelFileMagic valueOf(byte[] magic) {
        for (ExcelFileMagic fm : values()) {
            for (byte[] ma : fm.magic) {
                // don't try to match if the given byte-array is too short
                // for this pattern anyway
                if (magic.length < ma.length) {
                    continue;
                }

                if (findMagic(ma, magic)) {
                    return fm;
                }
            }
        }
        return UNKNOWN;
    }

    private static boolean findMagic(byte[] expected, byte[] actual) {
        int i = 0;
        for (byte expectedByte : expected) {
            if (actual[i++] != expectedByte && expectedByte != '?') {
                return false;
            }
        }
        return true;
    }

}
