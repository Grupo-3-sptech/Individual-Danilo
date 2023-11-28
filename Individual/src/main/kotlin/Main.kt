import com.github.britooo.looca.api.core.Looca
import com.github.britooo.looca.api.group.dispositivos.DispositivoUsb
import com.github.britooo.looca.api.group.temperatura.Temperatura
import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.time.LocalDate
import java.time.LocalDateTime
import javax.swing.JOptionPane
import java.io.File
import kotlin.concurrent.thread

fun main() {
    val looka1 = Main()
    looka1.all()
}

class Main {
    val looca = Looca()
    //    var bdInter: JdbcTemplate
    var bdInterServer: JdbcTemplate

    //id do processador de placeholder por enquanto.
    var id = Looca().processador.id

    init {
//        val dataSource = BasicDataSource()
//        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
//        val serverName = "localhost"
//        val mydatabase = "medconnect"
//        dataSource.username = "medconnect"
//        dataSource.password = "medconnect123"
//        dataSource.url = "jdbc:mysql://$serverName/$mydatabase"
//        bdInter = JdbcTemplate(dataSource)

        //server

        val dataSoruceServer = BasicDataSource()
        dataSoruceServer.url = "jdbc:sqlserver://52.7.105.138:1433;databaseName=medconnect;encrypt=false";
        dataSoruceServer.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        dataSoruceServer.username = "sa"
        dataSoruceServer.password = "medconnect123"
        bdInterServer = JdbcTemplate(dataSoruceServer)
    }


    fun all() {
        val idRobo1 = bdInterServer.queryForObject(
            """
    SELECT COUNT(*) AS count FROM RoboCirurgiao WHERE idProcess = '$id'
    """,
            Int::class.java,
        )

        if (idRobo1 == 0) {
            bdInterServer.execute(
                """
    INSERT INTO RoboCirurgiao (modelo, fabricacao, fkStatus, idProcess, fkHospital) 
    VALUES ('Modelo A', '${looca.processador.fabricante}', 1, '$id', 1)
""".trimIndent()
            )
        }else {


            while (true) {

                temperatura()
                Thread.sleep(20 * 1000)
            }
        }

    }


    fun temperatura() {
        val roboId = bdInterServer.queryForObject(
            """
    select idRobo from RoboCirurgiao where idProcess = '$id'
    """,
            Int::class.java,
        )

        val temperatura = looca.temperatura.temperatura

        bdInterServer.execute(
            """
        INSERT INTO registros (fkRoboRegistro, HorarioDado, dado, fkComponente)
        VALUES 
        ($roboId, 'não sei como faz horrio', '$temperatura', 5);
    """.trimIndent()
        )


    }


}
