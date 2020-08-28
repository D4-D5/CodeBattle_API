package com.example.CodeWar.services;

import com.example.CodeWar.services.ContestService;
import com.example.CodeWar.services.implementation.ContestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
public class RunnableTask implements Runnable{

    @Autowired
    private ContestService contestService;

    private String roomId;

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    //    public RunnableTask(String roomId){
//        this.roomId = roomId;
//    }

    @Override
    public void run() {
//        ContestServiceImpl contestService = new ContestServiceImpl();
//        String roomId = "YOYO";
        contestService.endContest(roomId);
    }
}