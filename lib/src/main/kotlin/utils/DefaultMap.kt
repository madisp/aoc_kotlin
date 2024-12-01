package utils

/**
 * A map that returns a default value when the key is not found.
 */
class DefaultMap<K, V>(
  private val backingMap: Map<K, V>,
  private val defaultValue: V,
) : Map<K, V> by backingMap {
  override fun get(key: K): V {
    return backingMap[key] ?: defaultValue
  }
}
