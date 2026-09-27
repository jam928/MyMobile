package db.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Passwords used to be stored in plain text. Replaces each one with its BCrypt hash,
 * so customers keep logging in with the same password.
 */
public class V8__Hash_existing_passwords extends BaseJavaMigration {

	@Override
	public void migrate(Context context) throws Exception {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		try (Statement select = context.getConnection().createStatement();
				ResultSet customers = select.executeQuery("SELECT cid, password FROM customer");
				PreparedStatement update = context.getConnection().prepareStatement("UPDATE customer SET password = ? WHERE cid = ?")) {
			while (customers.next()) {
				String password = customers.getString("password");
				// skip anything already encoded ("{bcrypt}...")
				if (password == null || password.startsWith("{"))
					continue;
				update.setString(1, encoder.encode(password));
				update.setInt(2, customers.getInt("cid"));
				update.addBatch();
			}
			update.executeBatch();
		}
	}
}
