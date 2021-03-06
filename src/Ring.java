import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Ring {
    private Node initNode = null;
    private Node leader;
    private Node linkNode; //the interface node that connect sub-ring and main ring
    private int numOfRounds = 0;
    private int numOfMessages = 0;
    List processorList = new ArrayList();

    public static List interfaceList = new ArrayList();
    public static List<Integer> randomNumberList = new ArrayList<>();
    public static List totalRing = new ArrayList(); //include main ring and sub-rings

    public Ring(int numOfProcessor) {
        totalRing.add(this);
        buildRing(numOfProcessor);
    }


    public Node getInitNode() {
        return initNode;
    }

    public Node getLeader() {
        return leader;
    }

    public void setLeader(Node leader) {
        this.leader = leader;
        leader.status = Node.Status.LEADER;
    }

    public Node getLinkNode() {
        return linkNode;
    }

    public void setLinkNode(Node linkNode) {
        this.linkNode = linkNode;
    }

    public int getNumOfRounds() {
        return numOfRounds;
    }

    public void setNumOfRounds(int numOfRounds) {
        this.numOfRounds = numOfRounds;
    }

    public int getNumOfMessages() {
        return numOfMessages;
    }

    public void setNumOfMessages(int numOfMessages) {
        this.numOfMessages = numOfMessages;
    }

    public void initProcessorList() {
        Node temp = this.getInitNode();
        while (true) {
            processorList.add(temp);
            temp = temp.next;
            if (temp == this.initNode) {
                break;
            }
        }
    }

    public void buildRing(int amount) {
        if (amount < 1) { //amount is the amount of nodes to be added
            System.out.println("Invalid amount.");
            return;
        }
        //current node (help to traverse the list)
        Node curNode = null;
        //use for loop to construct the circle list
        for (int i = 1; i <= amount; i++) {
            //node to be added
            //automatically be added in the order of num
            Node newNode = new Node(i, i);
            if (i == 1) {
                initNode = newNode;
                newNode.next = initNode; //form a circle
                curNode = initNode;
            } else {
                curNode.next = newNode;
                newNode.next = initNode;
                curNode = newNode; //move the assist variable curNode to newNode. same as(curNode=curNode.next)
            }
        }
        initProcessorList();
    }

    //processor number of a single ring
    public int getProcessorAmount(Node head) {
        Node temp = head;
        int num = 1;
        boolean loop = true;

        while (loop) {
            temp = temp.next;
            num++;
            if (temp.next == initNode) {
                loop = false;
            }
        }
        return num;
    }

    //discriminate between leader and non-leader
    public void updateStatus() {
        for (int i = 0; i < processorList.size(); i++) {
            Node node = (Node) processorList.get(i);
            if (node != leader) {
                node.status = Node.Status.subordinate;
            }
        }
    }

    //total processor numbers of all rings
    public static int getTotalNumOfProcessor() {
        int sum = 0;
        for (int i = 0; i < totalRing.size(); i++) {
            Ring ring = (Ring) totalRing.get(i);
            sum = sum + ring.getProcessorAmount(ring.initNode);
        }
        return sum;
    }

    public static int getTotalNumOfRounds() {
        int sum = 0;
        Ring mainRing = (Ring) totalRing.get(0);
        sum = mainRing.numOfRounds;
        return sum;
    }

    public static int getTotalNumOfMessages() {
        int sum = 0;
        for (int i = 0; i < totalRing.size(); i++) {
            Ring ring = (Ring) totalRing.get(i);
            sum = sum + ring.numOfMessages;
        }
        return sum;
    }

    //random number to arrange unique ID and decide who is interface processor
    public static void generateRandomNumber(int amount, int min, int max) {
        randomNumberList.clear();
        Random r = new Random();
        while (randomNumberList.size() != amount) {
            int num = r.nextInt(max - min) + min;
            if (!randomNumberList.contains(num)) {
                randomNumberList.add(num);
            }
        }
    }

    //distribute Random unique IDs over total rings
    public static void giveIdOverRings() {
        int amount = getTotalNumOfProcessor();
        int randomIndex = 0; //index in the randomNumberList
        generateRandomNumber(amount, 1, 2 * amount);

        for (int i = 0; i < totalRing.size(); i++) {
            Ring ring = (Ring) totalRing.get(i);
            Node temp = ring.getInitNode();
            boolean loop = true;
            while (loop) {
                temp.assignValue((int) randomNumberList.get(randomIndex));
                randomIndex++;
                temp = temp.next;
                if (temp == ring.getInitNode()) {
                    loop = false;
                }
            }
        }
    }

    //construct IDs ascending clockwise
    public static void giveIdAscendClockwise() {
        int amount = getTotalNumOfProcessor();
        generateRandomNumber(amount, 1, 3 * amount);
        int sum = 0;

        for (int i = 0; i < totalRing.size(); i++) {
            if (i == 0) {
                Ring ring = (Ring) totalRing.get(0);
                List ID_list = randomNumberList.subList(0, ring.processorList.size());
                Collections.sort(ID_list);
                for (int j = 0; j < ring.processorList.size(); j++) {
                    Node node = (Node) ring.processorList.get(j);
                    node.assignValue((Integer) ID_list.get(j));
                }
            } else {
                Ring ring_ahead = (Ring) totalRing.get(i - 1);
                Ring ring_current = (Ring) totalRing.get(i);
                sum = sum + ring_ahead.processorList.size();
                List ID_list = randomNumberList.subList(sum, sum + ring_current.processorList.size());
                Collections.sort(ID_list);
                for (int j = 0; j < ring_current.processorList.size(); j++) {
                    Node node = (Node) ring_current.processorList.get(j);
                    node.assignValue((Integer) ID_list.get(j));
                }
            }
        }
    }

    //construct IDs ascending counterclockwise
    public static void giveIdAscendCounterClockwise() {
        int amount = getTotalNumOfProcessor();
        generateRandomNumber(amount, 1, 3 * amount);
        int sum = 0;

        for (int i = 0; i < totalRing.size(); i++) {
            if (i == 0) {
                Ring ring = (Ring) totalRing.get(0);
                List ID_list = randomNumberList.subList(0, ring.processorList.size());
                Collections.sort(ID_list);
                Collections.reverse(ID_list);
                for (int j = 0; j < ring.processorList.size(); j++) {
                    Node node = (Node) ring.processorList.get(j);
                    node.assignValue((Integer) ID_list.get(j));
                }
            } else {
                Ring ring_ahead = (Ring) totalRing.get(i - 1);
                Ring ring_current = (Ring) totalRing.get(i);
                sum = sum + ring_ahead.processorList.size();
                List ID_list = randomNumberList.subList(sum, sum + ring_current.processorList.size());
                Collections.sort(ID_list);
                Collections.reverse(ID_list);
                for (int j = 0; j < ring_current.processorList.size(); j++) {
                    Node node = (Node) ring_current.processorList.get(j);
                    node.assignValue((Integer) ID_list.get(j));
                }
            }
        }
    }

    //randomly select some nodes as interface processors
    public static void generateInterfaceProcessor() {
        Ring mainRing = (Ring) totalRing.get(0);
        int numOfInterface = totalRing.size() - 1;
        int max = mainRing.getProcessorAmount(mainRing.getInitNode());
        int subRingIndex = 1;
        generateRandomNumber(numOfInterface, 1, max);

        Node temp = mainRing.getInitNode();
        while (true) {
            if (randomNumberList.contains(temp.index)) {
                temp.assignValue(-1);
                temp.progress = Node.Progress.sleeping; //interface node let it be asleep at first
                temp.awakeRound = -1;
                interfaceList.add(temp);
                temp.linkedRing = (Ring) totalRing.get(subRingIndex);
                Ring ring = (Ring) totalRing.get(subRingIndex);
                ring.setLinkNode(temp);
                subRingIndex++;
            }
            temp = temp.next;
            if (temp == mainRing.getInitNode()) {
                break;
            }
        }
    }

    //print the ring
    public void list() {
        //whether the list is empty
        if (initNode == null) {
            System.out.println("The linked list is empty.");
            return;
        }
        //use an assisting variable to traverse the list
        Node temp = initNode;
        while (true) {
            //output current node
            System.out.println(temp);
            temp = temp.next;
            //whether access the end of the list
            if (temp == initNode) { //have gone back to the initNode, then stop because it's a circle.
                break;
            }
        }
    }
}
