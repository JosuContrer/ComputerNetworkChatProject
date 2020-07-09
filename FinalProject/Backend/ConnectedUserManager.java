package FinalProject.Backend;

import java.util.LinkedList;

public class ConnectedUserManager {

    private static LinkedList<String> userList = new LinkedList<>();

    /**
     * Delete user form list.
     * @param userName
     * @return
     */
    public boolean deleteUser(String userName){
        for(String u: userList){
            if(userName.equals(u)){
                userList.remove(userList.indexOf(u));
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a user to the list.
     * @param userName
     * @return
     */
    public boolean addUser(String userName){
        if(!duplicateUser(userName)){ // Handle duplicate usernames
            userList.add(userName);
            return true;
        }
        return false;
    }

    /**
     * Checks for duplicate users
     * @param userName
     * @return
     */
    public boolean duplicateUser(String userName){
        for(String u: userList){
            if(userName.equals(u)){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns Linked List off all connected users.
     * @return
     */
    public LinkedList<String> getUserList(){
        return userList;
    }

    @Override
    public String toString(){
        String result = "";

        for(String s: userList){
            result = result + s + ",";
        }
        return result;
    }
}
