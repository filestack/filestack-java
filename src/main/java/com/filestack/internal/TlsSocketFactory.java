package org.filestack.internal;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class TlsSocketFactory extends SSLSocketFactory {

  private static final String[] TLS_1_2 = {"TLSv1.2"};

  private final SSLSocketFactory factory;

  TlsSocketFactory(SSLSocketFactory factory) {
    this.factory = factory;
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return factory.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return factory.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
    return patch(factory.createSocket(socket, s, i, b));
  }

  @Override
  public Socket createSocket(String s, int i) throws IOException {
    return patch(factory.createSocket(s, i));
  }

  @Override
  public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException {
    return patch(factory.createSocket(s, i, inetAddress, i1));
  }

  @Override
  public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
    return patch(factory.createSocket(inetAddress, i));
  }

  @Override
  public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
    return patch(factory.createSocket(inetAddress, i, inetAddress1, i1));
  }

  private Socket patch(Socket s) {
    if (s instanceof SSLSocket) {
      ((SSLSocket) s).setEnabledProtocols(TLS_1_2);
    }
    return s;
  }
}
