package codingblackfemales.collection.extrusive;

public interface IntToObjMap<TYPEOF> {
    TYPEOF get(int i);
    void put(int i, TYPEOF obj);
    TYPEOF getOrDefault(int i, TYPEOF obj);
    void forEach(IntObjConsumer<TYPEOF> consumer);
}
