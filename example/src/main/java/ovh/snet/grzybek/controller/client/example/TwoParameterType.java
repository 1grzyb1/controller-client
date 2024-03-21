package ovh.snet.grzybek.controller.client.example;

class TwoParameterType<T, K> {
  private final T first;
  private final K second;

  public TwoParameterType(T first, K second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public K getSecond() {
    return second;
  }
}
