Simple-Tasker
========

An easy to use and very simple tasking app, you can set a location or an hour to active a personalized task! [Steel under development].

How it Works?
=
![Demostration](screen/gif.gif)

### Details
Simple-Tasker use an sqlite database to store the task with its coordinates and name.
Each task is connected, by using its id, with a `.xml` file that contains all the operations that the software is going to do when the task will be actived.

### Example
this is an example of how a standard configuration file is made (this for example set the screen brightness to 78%).
```xml
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<Actions>
    <disturb>0</disturb>
    <lumin>78</lumin>
    <appstart>0</appstart>
    <planemode>0</planemode>
    <battery>0</battery>
    <lockscrn>0</lockscrn>
    <notify>nulla</notify>
    <message>nulla</message>
    <mail>nulla</mail>
</Actions>
```

#### More?

If you want to know more about the project or you just have to ask me something, you can contact me at `giacobbealessio@gmail.com`.

