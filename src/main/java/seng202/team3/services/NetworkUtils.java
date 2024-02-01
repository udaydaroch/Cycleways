package seng202.team3.services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/** A utility class providing methods related to network operations. */
public class NetworkUtils {

  /**
   * Checks if an active internet connection is available by trying to connect to OpenStreetMap's
   * website.
   *
   * @return true if the internet is available; false otherwise.
   */
  public static boolean isInternetAvailable() {
    try (Socket socket = new Socket()) {
      // check if can connect with OpenStreetMap's website
      socket.connect(new InetSocketAddress("www.openstreetmap.org", 80), 1000);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
