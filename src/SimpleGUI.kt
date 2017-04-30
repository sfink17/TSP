/**
 * Created by Simon on 3/15/2017.
 */
import java.awt.*
import java.awt.event.*
import java.io.InputStream
import javax.swing.*
import javax.swing.border.Border
import javax.swing.text.JTextComponent

class SimpleGUI : JPanel(), ActionListener {

    private var instance = ""
    private var heuristic = ""

    override fun actionPerformed(e: ActionEvent) {
        val command = e.actionCommand
        if (command[0] == 'I') instance = "instances/" + command.substring(1) + ".txt"
        else if (command[0] == 'H') heuristic = command.substring(1)
        else if (instance.length != 0 && heuristic.length != 0){

            val istream = this.javaClass.getResourceAsStream(instance)
            toPoints(istream, instance.substringAfter('/').substringBefore('.'), heuristic)
        }
        else System.err.println("Please ensure that both fields are selected")
    }

    init {

        val textHeader = JLabel("Select TSP instance and heuristic")
        textHeader.border = BorderFactory.createEmptyBorder(10, 150, 10 , 150)
        textHeader.alignmentX = Component.CENTER_ALIGNMENT

        val startButton = JButton("Start")


        val iLabel = JLabel("Instances:")

        val tsp100 = JRadioButton("tsp100")
        tsp100.actionCommand = "Itsp100"

        val tsp1000 = JRadioButton("tsp1000")
        tsp1000.actionCommand = "Itsp1000"


        val mona_20k = JRadioButton("mona-20k")
        mona_20k.actionCommand = "Imona-20k"

        val usa13509 = JRadioButton("usa13509")
        usa13509.actionCommand = "Iusa13509"

        val tsp85900 = JRadioButton("tsp85900")
        tsp85900.actionCommand = "Itsp85900"

        val hLabel = JLabel("Heuristics:")

        val nearestNeighbor = JRadioButton("Nearest Neighbor")
        nearestNeighbor.actionCommand = "Hnn"

        val nearestInsertion = JRadioButton("Nearest Insertion")
        nearestInsertion.actionCommand = "Hni"

        val greedy = JRadioButton("Greedy")
        greedy.actionCommand = "Hgreedy"

        val LK = JRadioButton("LK")
        LK.actionCommand = "Hlk"

        val instanceGroup = ButtonGroup()
        instanceGroup.add(tsp100)
        instanceGroup.add(tsp1000)
        instanceGroup.add(mona_20k)
        instanceGroup.add(usa13509)
        instanceGroup.add(tsp85900)

        val heuristicGroup = ButtonGroup()
        heuristicGroup.add(nearestNeighbor)
        heuristicGroup.add(nearestInsertion)
        heuristicGroup.add(greedy)
        heuristicGroup.add(LK)

        val instances = JPanel(GridLayout(0, 1))
        instances.add(tsp100)
        instances.add(tsp1000)
        instances.add(mona_20k)
        instances.add(usa13509)
        instances.add(tsp85900)

        val heuristics = JPanel(GridLayout(0, 1))
        heuristics.add(nearestNeighbor)
        heuristics.add(nearestInsertion)
        heuristics.add(greedy)
        heuristics.add(LK)

        tsp100.addActionListener(this)
        tsp1000.addActionListener(this)
        mona_20k.addActionListener(this)
        usa13509.addActionListener(this)
        tsp85900.addActionListener(this)
        nearestNeighbor.addActionListener(this)
        nearestInsertion.addActionListener(this)
        greedy.addActionListener(this)
        LK.addActionListener(this)
        startButton.addActionListener(this)



        val content = JPanel()
        val groupLayout = GroupLayout(content)
        content.layout = groupLayout
        groupLayout.autoCreateGaps = true
        groupLayout.autoCreateContainerGaps = true

        val hgroup = groupLayout.createSequentialGroup()
        val vgroup = groupLayout.createSequentialGroup()

        hgroup.addGroup(groupLayout.createParallelGroup().addComponent(iLabel).addComponent(instances))
        hgroup.addGroup(groupLayout.createParallelGroup().addComponent(hLabel).addComponent(heuristics))
        groupLayout.setHorizontalGroup(hgroup)

        vgroup.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iLabel).addComponent(hLabel))
        vgroup.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(instances).addComponent(heuristics))
        groupLayout.setVerticalGroup(vgroup)

        val app = JPanel(BorderLayout())
        app.add(textHeader, BorderLayout.NORTH)
        app.add(content, BorderLayout.CENTER)
        app.add(startButton, BorderLayout.SOUTH)

        val window = JFrame("TSP Heuristics")
        window.contentPane = app
        window.pack()
        window.isVisible = true

    }
}