package bgu.spl.net.api;


import bgu.spl.net.api.bidi.Messages.*;
import bgu.spl.net.api.bidi.Message;


import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class EncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private int op=0;
    private int stage=0;
    private int counter=0;
    private LinkedList<Byte> msg=new LinkedList<>();
    private byte[] tmpArray=new byte[2];    //Array for temporary use
    private boolean msgReady=false;



    @Override
    public Message decodeNextByte(byte nextByte) {
        msg.add(nextByte);
        switch(op){
            case 0:getOP(nextByte);
                break;
            case 1:getMessageOf2Stages(nextByte);
                break;
            case 2:getMessageOf2Stages(nextByte);
                break;
//            case 3:getOpMessage();
//                break;
            case 4:getFollow(nextByte);
                break;
            case 5:getMessageOfSingleStage(nextByte);
                break;
            case 6:getMessageOf2Stages(nextByte);
                break;
//            case 7:getOpMessage();
//                break;
            case 8:getMessageOfSingleStage(nextByte);
                break;
        }
        if(op==3 || op==7)
            getOpMessage();
        if(msgReady){
            int tmpOp=op;
            byte[] output=getByteArray(msg);
            resetDecoder();
            switch (tmpOp){
                case 1:return decodeObjectOf2Strings(output,1);
                case 2:return decodeObjectOf2Strings(output,2);
                case 3:return new Logout();
                case 4:return decodeFollowMessage(output);
                case 5:return decodeObjectOfSingleString(output,5);
                case 6:return decodeObjectOf2Strings(output,6);
                case 7:return new UserList();
                case 8:return decodeObjectOfSingleString(output,8);

            }
        }
        return null;
    }

    private Message decodeObjectOfSingleString(byte[] byteArr, int msgOp) {
        switch(msgOp){
            case 8:  return new Stat(new String(byteArr,2,byteArr.length-3, StandardCharsets.UTF_8));
            default: return new Post(new String(byteArr,2,byteArr.length-3, StandardCharsets.UTF_8));
        }

    }

    /**
     * Resetting the decoder fields
     */
    private void resetDecoder() {
        op=0;
        stage=0;
        counter=0;
        msg.clear();
        tmpArray=new byte[2];
        msgReady=false;
    }


    private void getOP(byte nextByte) {
        tmpArray[counter]=nextByte;
        counter++;
        if(counter==2){
            op=bytesToShort(tmpArray,0);
            counter=0;
            stage++;
        }
    }

    /**
     * Handle messages known to have only two zero bytes
     * @param nextByte
     */
    private void getMessageOf2Stages(byte nextByte) {
        switch(stage){
            case 1: if(nextByte==0) stage++;
                break;
            case 2: if(nextByte==0) msgReady=true;
                break;
        }
    }

    /**
     * Handle messages which contains only the op.
     * for Logout and UserList
     */
    private void getOpMessage() {
        msgReady=true;
    }

    private void getFollow(byte nextByte) {
        switch(stage){
            case 1:{
                if(counter>0){
                    tmpArray[counter-1]=nextByte;
                    counter++;
                    if(counter==3){
                        counter=bytesToShort(tmpArray,0);
                        stage++;
                    }
                }else{
                    counter++;
                }
            }break;
            case 2:{
                if(nextByte==0)
                    counter--;
                if(counter==0)
                    msgReady=true;
            }break;
        }
    }

    /**
     * handles messages known to have only one zero byte
     * @param nextByte
     */
    private void getMessageOfSingleStage(byte nextByte) {
        if(nextByte==0)
            msgReady=true;
    }



    /**
     * this static method is used in the Message object to decode msg of 2string divided by zero byte.
     * @param msg
     * @return pair of string by the order they appear in the msg.
     */
    private Message decodeObjectOf2Strings(byte[] msg,int msgOp){
        String first="";
        String second="";
        boolean stop=false;
        int i=2;
        int indexOfNextString=2;
        int sizeOfNextString=0;
        for(int j=0;j<=1;j++) {
            for (; i < msg.length & !stop; i++) {
                if (msg[i] == 0)
                    stop = true;
                else {
                    sizeOfNextString++;
                }
            }
            switch(j){
                case 0:first=new String(msg,indexOfNextString,sizeOfNextString, StandardCharsets.UTF_8);
                    break;
                case 1:second=new String(msg,indexOfNextString,sizeOfNextString, StandardCharsets.UTF_8);
                    break;
            }
            stop=false;
            indexOfNextString=i;
            sizeOfNextString=0;
        }
        switch (msgOp){
            case 1:return new Register(first,second);
            case 2:return new Login(first,second);
            default:return new Pm(first,second);
        }
    }

    private Message decodeFollowMessage(byte[] output) {
        boolean follow=false;
        if(output[2]==0)
            follow=true;
        int numOfUsers=bytesToShort(output,3);
        String[] userList=decodeToStringArray(output,5,numOfUsers);
        return new Follow(follow,numOfUsers,userList);
    }

    private String[] decodeToStringArray(byte[] byteArr,int startIndex,int numOfStrings){
        LinkedList<String> list=new LinkedList<>();
        boolean stop=false;
        int i=startIndex;
        int indexOfNextString=startIndex;
        int sizeOfNextString=0;
        for(int j=0;j<=numOfStrings;j++) {
            for (; i < byteArr.length & !stop; i++) {
                if (byteArr[i] == 0)
                    stop = true;
                else {
                    sizeOfNextString++;
                }
            }
            if(indexOfNextString<byteArr.length)
                list.add(new String(byteArr,indexOfNextString,sizeOfNextString, StandardCharsets.UTF_8));
            stop=false;
            indexOfNextString=i;
            sizeOfNextString=0;
        }

        return getStringArray(list);
    }

    @Override
    public byte[] encode(Message message) {
        Object[] msgDataSection=message.getMessageObjects();
        //Handle the ack
        switch((short)msgDataSection[0]){
            case 9:return notificationEncode(msgDataSection);
            case 10:switch((short)msgDataSection[1]){
                case 4:return numAndStringEncode(msgDataSection);
                case 7:return numAndStringEncode(msgDataSection);
                case 8:return numsEncoding(msgDataSection,5);
                default:return numsEncoding(msgDataSection,2);
            }
        }
        //Handle Error
        return numsEncoding(msgDataSection,2);
    }

    /**
     * encode Notification message to bytes of stracture:
     * [int op][char pm/public][string][0-byte][string][0-byte]
     * @param msgDataSection
     * @return bytes array of encoded Notification message
     */
    private byte[] notificationEncode(Object[] msgDataSection) {
        LinkedList<Byte> output=new LinkedList<>();
        for (Byte b : shortToBytes((short) msgDataSection[0]))
            output.add(b);
        output.add(getPostTypeByte(msgDataSection[1]));
        for(int i=2;i<=3;i++) {
            for (byte b : ((String)msgDataSection[i]).getBytes())
                output.add(b);
            output.add((byte)0);
        }
        return getByteArray(output);
    }


    private Byte getPostTypeByte(Object data) {
        byte postType=0;
        if((short)data==1)
            postType=1;
        return postType;

//        String postType="0";
//        if((short)data==1)
//            postType="1";
//        byte[] bytesOfType=postType.getBytes();
//        return bytesOfType[0];
    }

    /**
     * gets the array of the ack data and encode as much nums as "numOfSections"
     * @param msgDataSection
     * @param numOfSections
     * @return encoded message.
     */
    private byte[] numsEncoding(Object[] msgDataSection,int numOfSections) {
        LinkedList<Byte> output=new LinkedList<>();
        for(int i=0;i<numOfSections;i++) {
            short num=(short)msgDataSection[i];
            for (Byte b : shortToBytes(num))
                output.add(b);
        }
        return getByteArray(output);
    }

    /**
     * encoding msg of stracture [ack_op][op][numOfUsers][UserNameList][0]
     * @param msgDataSection
     * @return  encoded message
     */
    private byte[] numAndStringEncode(Object[] msgDataSection) {
        LinkedList<Byte> output=new LinkedList<>();
        for(int i=0;i<3;i++) {
            for (Byte b : shortToBytes((short) msgDataSection[i]))
                output.add(b);
        }
        for(String name:(LinkedList<String>)msgDataSection[3]) {
            for (byte b : name.getBytes())
                output.add(b);
            output.add((byte) 0);
        }
        return getByteArray(output);
    }

    /**
     *
     * @return byte array with next message
     */
    private byte[] getByteArray(LinkedList<Byte> list) {
        byte[] output=new byte[list.size()];
        int i=0;
        for (Byte b:list) {
            output[i] = b;
            i++;
        }
        return output;
    }

    private String[] getStringArray(LinkedList<String> list) {
        String[] output=new String[list.size()];
        int i=0;
        for (String s:list) {
            output[i] = s;
            i++;
        }
        return output;
    }

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private short bytesToShort(byte[] byteArr,int index){
        short result = (short)((byteArr[index] & 0xff) << 8);
        result += (short)(byteArr[index+1] & 0xff);
        return result;
    }
}
