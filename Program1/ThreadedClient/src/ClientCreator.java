import edu.nku.client.Client;

public class ClientCreator {

	static public void main(String[] args) {
		final int PORT = 4448;

		Client client0 = new Client(0, PORT);
		Client client1 = new Client(1, PORT);
		Client client2 = new Client(2, PORT);
		client0.start();
		client1.start();
		client2.start();
	}
}
