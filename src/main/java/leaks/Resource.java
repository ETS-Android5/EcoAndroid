package leaks;

import java.util.Arrays;

/**
 * Representation of an Android resource.
 */
public enum Resource {

    /**
     * Representation of the Cursor resource.
     * A Cursor represents the result of a query to a database.
     */
    CURSOR ("android.database.Cursor",
            new String[]{"rawQuery","query"}, new String[]{"android.database.sqlite.SQLiteDatabase"},
            new String[]{"close"}, new String[]{"android.database.Cursor"},
            "isClosed", "#NONE",
            true, true),

    /**
     * Representation of the Wakelock resource.
     * A Wakelock is used to prevent the device from going to sleep, usually to perform critical operations.
     */
    WAKELOCK ("android.os.PowerManager$WakeLock",
            new String[]{"acquire"}, new String[]{"android.os.PowerManager$WakeLock"},
            new String[]{"release"}, new String[]{"android.os.PowerManager$WakeLock"},
            "isHeld", "onPause",
            true, true),

    /**
     * Representation of the SQLiteDatabase resource.
     * An SQLiteDatabase is used to store and retrieve data. Usually used together with a Cursor.
     */
    SQLITEDB ("android.database.sqlite.SQLiteDatabase",
            new String[]{"getWritableDatabase", "getReadableDatabase"}, new String[]{"android.database.sqlite.SQLiteOpenHelper"},
            new String[]{"close"}, new String[]{"android.database.sqlite.SQLiteClosable", "android.database.sqlite.SQLiteOpenHelper", "android.database.sqlite.SQLiteDatabase"},
            "#NONE", "#NONE",
            true, true),

    /*
    BUFFERED_INPUT_STREAM ("java.io.BufferedInputStream",
            new String[]{"<init>"}, "java.io.BufferedInputStream",
            new String[]{"close"}, "java.io.BufferedInputStream",
            "#NONE", "#NONE",
            true, false),

     */

    /**
     * Representation of the Camera resource.
     */
    CAMERA ("android.hardware.Camera",
            new String[]{"lock", "open", "startFaceDectection", "startPreview"}, new String[]{"android.hardware.Camera"},
            new String[]{"unlock", "close", "stopFaceDetection", "stopPreview"}, new String[]{"android.hardware.Camera"},
            "#NONE", "surfaceDestroyed",
            true, true);

    private final String type;
    private final String[] acquireOp;
    private final String[] acquireClass;
    private final String[] releaseOp;
    private final String[] releaseClass;
    private final String heldCheckOp;
    private final String placeToRelease;
    private final boolean intraProcedural;
    private final boolean interProcedural;

    /**
     * For the context of resource leak detection, an Android resource can be represented
     * as it follows.
     * @param type Class of the resource
     * @param acquireOp Operations used to release the resource
     * @param acquireClass Classes of the object where the acquire operation is invoked
     * @param releaseOp Operations used to release the resource
     * @param releaseClass Classes of the object where the release operation is invoked
     * @param heldCheckOp Operation used to check if resource is acquired
     *                    (#NONE if no such operation exists)
     * @param placeToRelease Suggested callback to release the (inter-procedural) resource
     *                       (#NONE if no suggested place exists)
     * @param intraProcedural
     * @param interProcedural
     */
    Resource(String type, String[] acquireOp, String[] acquireClass, String[] releaseOp, String[] releaseClass,
             String heldCheckOp, String placeToRelease, boolean intraProcedural, boolean interProcedural) {
        this.type = type;
        this.acquireOp = acquireOp;
        this.acquireClass = acquireClass;
        this.releaseOp = releaseOp;
        this.releaseClass = releaseClass;
        this.heldCheckOp = heldCheckOp;
        this.placeToRelease = placeToRelease;
        this.intraProcedural = intraProcedural;
        this.interProcedural = interProcedural;
    }

    public String getType() {
        return type;
    }

    public boolean isIntraProcedural() {
        return intraProcedural;
    }

    public boolean isInterProcedural() {
        return interProcedural;
    }

    public boolean isBeingAcquired(String acquireOp, String acquireClass) {
        boolean acquireOpMatch = false;
        for (String op : this.acquireOp) {
            if (acquireOp.equals(op)) {
                acquireOpMatch = true;
            }
        }

        boolean acquireClassMatch = false;
        for (String c : this.acquireClass) {
            if (acquireClass.equals(c)) {
                acquireClassMatch = true;
            }
        }
        return acquireOpMatch && acquireClassMatch;
    }

    public boolean isBeingReleased(String releaseOp, String releaseClass) {
        boolean releaseOpMatch = false;
        for (String op : this.releaseOp) {
            if (releaseOp.equals(op)) {
                releaseOpMatch = true;
            }
        }

        boolean releaseClassMatch = false;
        for (String c : this.releaseClass) {
            if (releaseClass.equals(c)) {
                releaseClassMatch = true;
            }
        }
        return releaseOpMatch && releaseClassMatch;
    }

    public boolean isBeingDeclared(String type) {
        return this.type.equals(type);
    }

    public boolean isCheckedIfItsHeld(String type, String heldCheckOp) {
        return this.type.equals(type) && this.heldCheckOp.equals(heldCheckOp);
    }

    public String getPlaceToRelease() {
        return placeToRelease;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
