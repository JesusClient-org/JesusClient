package cum.jesus.jesusclient.file;

/**
 * Interface that allows a class to be stored and retrieved from a .jesus data file
 */
public interface JesusSerializable {
    /**
     * Serialize the object to an array of bytes for easy storage
     * @return The bytes holding the object
     */
    byte[] toBytes();

    /**
     * Loads the object from its serialized bytes
     * @param bytes The array of bytes containing the object
     * @param index The index to start from when reading
     * @return The new index after reading
     */
    int fromBytes(byte[] bytes, int index);
}
