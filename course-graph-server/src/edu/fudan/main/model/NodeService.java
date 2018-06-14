package edu.fudan.main.model;

import edu.fudan.main.domain.*;
import edu.fudan.main.dto.response.QuestionResp;
import edu.fudan.main.exception.GraphNotFoundException;
import edu.fudan.main.exception.NodeNotFoundException;
import edu.fudan.main.repository.GraphRepository;
import edu.fudan.main.repository.NodeRepository;
import edu.fudan.main.repository.QuestionRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NodeService {


    private final NodeRepository nodeRepository;
    private final GraphRepository graphRepository;
    private final QuestionRepository questionRepository;

    public NodeService(NodeRepository nodeRepository, GraphRepository graphRepository, QuestionRepository questionRepository) {
        this.nodeRepository = nodeRepository;
        this.graphRepository = graphRepository;
        this.questionRepository = questionRepository;
    }

    public void deleteNode(String NodeId) {

    }

    /**
     * update all nodes related to one graph according to new jsmind data
     *
     * @param graphId    id of the graph
     * @param jsMindData new jsmind data
     */
    public void updateNodes(long graphId, String jsMindData) {
        //parse json string to json object
        JSONObject mindObject = new JSONObject(jsMindData);
        JSONObject mindData = (JSONObject) mindObject.get("data");

        //get existing nodes related to the graph
        Set<Node> oldNodes = graphRepository.findById(graphId).orElseThrow(
                GraphNotFoundException::new
        ).getNodeSet();

        //update old nodes and create new nodes
        List<Node> newNodes = new ArrayList<>();
        getNodesFromMindData(mindData, newNodes);
        //save() will merge new node with old node if it existed before, otherwise add it to the database
        nodeRepository.saveAll(newNodes);

        //find deleted nodes and delete them from database
        List<String> newNodeIds = new ArrayList<>();
        for (Node node : newNodes) {
            newNodeIds.add(node.getNodeId());
        }
        for (Node node : oldNodes) {
            if (!newNodeIds.contains(node.getNodeId())) {
                this.deleteNode(node.getNodeId());
            }
        }


    }

    /**
     * get all nodes in this mind-map
     *
     * @param mindNode the root of the mind-map json object
     * @param newNodes a list to save all nodes in this mind-map
     */
    private void getNodesFromMindData(JSONObject mindNode, List<Node> newNodes) {
        Node node = new Node((String) mindNode.get("id"), (String) mindNode.get("topic"));
        newNodes.add(node);
        if (mindNode.has("children")) {
            for (JSONObject subNode : (JSONObject[]) mindNode.get("children")) {
                getNodesFromMindData(subNode, newNodes);
            }
        }
        return;
    }

    /**
     * list all questions of the user
     *
     * @param currentUser
     * @param nodeId
     * @return list of question information
     */
    public List<QuestionResp> getAllQuestionsOfNode(User currentUser, String nodeId) {
        //get course node
        Node node = nodeRepository.findById(nodeId).orElseThrow(
                NodeNotFoundException::new
        );

        //get all questions related to the node
        List<Question> questions = node.getQuestionList();

        //generate question response
        List<QuestionResp> questionRespList = new ArrayList<>();
        for (Question q : questions) {
            QuestionResp questionResp = new QuestionResp(q, currentUser.getType());
            questionRespList.add(questionResp);
        }

        return questionRespList;
    }

    /**
     * create a new multiple-choice and add it to a course node
     * @param nodeId id of course node
     * @param description description of the question to be added
     * @param choices choices of the question to be added
     * @param answer the answer of this multiple-choice question
     * @return response information of the question created newly
     */
    public QuestionResp addQuestionMultipleChoiceOfNode(String nodeId, String description, List<Choice> choices, String answer) {
        //get the course node
        Node node = nodeRepository.findById(nodeId).orElseThrow(
                NodeNotFoundException::new
        );

        //create a new multiple-choice question
        Question question = new QuestionMultipleChoice(RandomIdGenerator.getInstance().generateRandomLongId(questionRepository),
                description, choices, answer);

        //add it to the node
        node.addQuestion(question);

        //save the changes in the database
        nodeRepository.save(node, 0);
        questionRepository.save(question, 0);

        //return question response
        return new QuestionResp(question, UserType.TEACHER);
    }

    /**
     * create a new short-answer and add it to a course node
     * @param nodeId id of the course node
     * @param description description of the question
     * @return response information of the question created newly
     */
    public QuestionResp addQuestionShortAnswerOfNode(String nodeId, String description){
        //get the course node
        Node node = nodeRepository.findById(nodeId).orElseThrow(
                NodeNotFoundException::new
        );

        //create a new short-answer question
        Question question = new QuestionShortAnswer(RandomIdGenerator.getInstance().generateRandomLongId(questionRepository),
                description);

        //add it to the node
        node.addQuestion(question);

        //save the changes in the database
        nodeRepository.save(node, 0);
        questionRepository.save(question, 0);

        //return question response
        return new QuestionResp(question, UserType.TEACHER);
    }

    /**
     * get the result (true or false) of user's answers of some questions
     * @param questions the questions that user answered
     * @return a list of results (true or false)
     */
    public void  getResultOfQuestionSubmission(List<Question> questions){

    }


}
