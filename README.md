strike
======

benchmark stress test tool

consists of core and maven plugin

The tool is designed according to the EDA model, can be used for benchmark test or long time pressure test, consist of 2 modules: the core module and the maven plugin model.
The full name of EDA is Event-Driven Architecture, a software architecture pattern. 
In this pattern , there is a scheduler to product, detect, arrange the events, and event handlers just handle the interested events.

There are about 5 kinds of events: Setup event, Warmup event, Run event, Cooldown event, End event.
When the setup event is triggered by the scheduler, the tool will init the resource, such as the thread workers, thread pools, object containers. And then the warmup event is triggered, the pre-test will be active, verify the test whether could be continued. In the setup event, the test is executed , and the test result is collected by the reportors. The statistics will be generated by the cooldown event handler.

The user can decide all the tests to run in a jvm or each test to run in a single fork jvm for avoiding object pollution.