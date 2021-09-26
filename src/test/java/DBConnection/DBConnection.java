package DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {

    public static void main(String[] args) {

        /*
        Es necesario cambiar el host del usuario root para poder acceder a la bbdd
        - Entrar en bbdd.
        - use mysql
        - update user set host='%'where user='root';
        Comprobar que se ha cambiado el host de root a %
        - update user set host='%'where user='root';
        Salir de bbdd y reiniciar el servicio
         */

        try {
            // Creamos conexion a la bbdd
            Connection connection = DriverManager.getConnection ("jdbc:mysql://URL:3306/NOMBREBBDD?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", "root", "pass");

            // Creamos el objeto Statement
            Statement statement = connection.createStatement ();

            // Ejecutamos una query
            ResultSet resultSet = statement.executeQuery ("SELECT nombre FROM tabla");

            // Leer el resulset
            while (resultSet.next ()) {
                System.out.println ( resultSet.getString ("nombre") );
                connection.close ();
            }

        } catch (Exception e) {
            e.printStackTrace ( );
        }
    }
}
