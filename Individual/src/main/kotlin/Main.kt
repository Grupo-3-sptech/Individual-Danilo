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
    val looka1 = LookaDados()
    looka1.all()
}

class LookaDados {
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

        println(id)

        while (true) {
            //   python()
            sistema()
            memoria()
            processador()
            grupoDeDiscos()
            grupoDeServicos()
            grupoDeProcessos()
            Dispositivo()
            rede()
            temperatura()
            Thread.sleep(20 * 1000)
        }

    }

    //fun python(){
    //var arqPyConn = "SolucaoConn.py"
    //var pyExec: Process? = null
    //pyExec = Runtime.getRuntime().exec("python $arqPyConn")
    //}


    fun cadastroUsu() {
        var autorizacao = false

        var email: String = JOptionPane.showInputDialog("insira o seu email")
        var senha: String = JOptionPane.showInputDialog("insira sua senha")


        var fkHospital = bdInterServer.queryForObject(
            """
    SELECT fkHospital FROM Usuario WHERE email = '$email' AND senha = '$senha'
    """,
            Int::class.java
        )


        if (fkHospital != null) {
            autorizacao = true
        }


        if (autorizacao == true) {
            JOptionPane.showMessageDialog(
                null,
                "arraste o get-pip.py para a pasta public execute o arquivo InstalarPython.bat como adimistrador em seguida o InstalarPip.bat ambos como adimistrador, a instalao já está começando"
            )
//vamos ter que pensar regra de negocio ou script para o python ser instalado "aqui"
            cad(fkHospital)
        } else {
            println("problema na autenticação")
        }
    }

    fun cad(fkHospital: Int) {

        bdInterServer.execute(
            """
    INSERT INTO RoboCirurgiao (modelo, fabricacao, fkStatus, idProcess, fkHospital) 
    VALUES ('Modelo A', '${looca.processador.fabricante}', 1, '$id', $fkHospital)
""".trimIndent()
        )

        println("parabéns robo cadastrado baixando agora a solução MEDCONNECT")


        all()
    }


    fun downloadArq(url: URL, nomeArquivoDoPip: String) {
//funão de baixar arquivo da net
        url.openStream().use {
            Channels.newChannel(it).use { rbc ->
                FileOutputStream(nomeArquivoDoPip).use { fos -> //
                    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE) //
                }
            }
        }
    }


    fun ver(): Boolean {
        //função que verifica se a maquina já foi usada antes


        val idRobo = bdInterServer.queryForObject(
            """
    SELECT COUNT(*) AS count FROM RoboCirurgiao WHERE idProcess = '$id'
    """,
            Int::class.java,
        )

        if (idRobo == 0) {
            return true
        } else {
            return false
        }


    }


    fun sistema() {
        val sistema = looca.sistema

        println(sistema)

        var fabricante = sistema.fabricante


        var incializando = sistema.inicializado

        var sistemaOperacional = sistema.sistemaOperacional

        println(looca.sistema.sistemaOperacional)

        var arquitetura = sistema.arquitetura

        var permissao = sistema.permissao

        var tempDeAtividade = sistema.tempoDeAtividade


    }

    fun memoria() {
        val memoria = looca.memoria

        var emUso = memoria.emUso

        var disponivel = memoria.disponivel

        var total = memoria.total
    }

    fun processador() {

        var processador = looca.processador

        var fabricante = processador.fabricante


        var frequencia = processador.frequencia

        var nome = processador.nome


        var identificador = processador.identificador


        var microarquitetura = processador.microarquitetura

        var numeroCpuFis = processador.numeroCpusFisicas

        var numCpuLogica = processador.numeroCpusLogicas

        var uso = processador.uso

        var numPacotFisico = processador.numeroPacotesFisicos


    }

    fun grupoDeDiscos() {
        val grupoDeDiscos = looca.grupoDeDiscos


        var qtdDeDisco = grupoDeDiscos.quantidadeDeDiscos
        //I live in the Rua hadock lobo Building on West 595 Street on the 2nd floor. My name is Enzo I’m 18 years old. There is an idea of a Enzo. Some kind of abstraction. But there is no real me.

        var discos = grupoDeDiscos.discos

        var volumes = grupoDeDiscos.volumes

        var tamanhoTotal = grupoDeDiscos.tamanhoTotal

        var qtdVolumes = grupoDeDiscos.quantidadeDeVolumes

        var nome = discos[0].nome

        var serial = discos[0].serial

    }

    fun grupoDeServicos() {
        val grupoDeServicos = looca.grupoDeServicos
        var servicos = grupoDeServicos.servicos
        var nome = servicos[0].nome
        var estado = servicos[0].estado
        var pid = servicos[0].pid
        var servicosAtivos = grupoDeServicos.servicosAtivos
        var sevicosInativos = grupoDeServicos.servicosInativos
        var totalDeServiços = grupoDeServicos.totalDeServicos
        var totalServicosAtivos = grupoDeServicos.totalServicosAtivos
        var totalServicosInativos = grupoDeServicos.totalServicosInativos
    }

    fun grupoDeProcessos() {
        val grupoDeProcessos = looca.grupoDeProcessos
        var processos = grupoDeProcessos.processos
        var totalProcessos = grupoDeProcessos.totalProcessos
        var totalThreads = grupoDeProcessos.totalThreads
    }


    fun Dispositivo() {
        val DispositivoUsbGp = looca.dispositivosUsbGrupo
        var totalConectados = DispositivoUsbGp.totalDispositvosUsbConectados
        var dispositivosUsb = DispositivoUsbGp.dispositivosUsb
        var dispositivosUsbConectados = DispositivoUsbGp.dispositivosUsbConectados
        var totalDispositvosUsb = DispositivoUsbGp.totalDispositvosUsb

        val idRobo = bdInterServer.queryForObject(
            """
    select idRobo from RoboCirurgiao where idProcess = '$id'
    """,
            Int::class.java,
        )

        for (dispositivo in dispositivosUsb) {
            var nome = dispositivo.nome
            var idProduto = dispositivo.idProduto
            var fornecedor = dispositivo.forncecedor


        }
    }


    fun rede() {
        val rede = looca.rede
        println(rede)
        var parametros = rede.parametros
        var grupoDeInterfaces = rede.grupoDeInterfaces
    }

    fun temperatura() {
        val temperatura = looca.temperatura.temperatura

        """
            select * from registros where fkComponente = 5 and fkRoboRegistro = '$id'
        """.trimIndent();

    }


}
