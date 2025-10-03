#include <omnetpp.h>
#include "DroneMessage_m.h"

using namespace omnetpp;

class Drone : public cSimpleModule
{
  private:
    int droneId;
    int totalLDs;

  protected:
    virtual void initialize() override {
        droneId = getIndex();
        totalLDs = par("totalLDs").intValue();

        scheduleAt(simTime() + uniform(0, 1), new cMessage("sendTimer"));
    }

    virtual void handleMessage(cMessage *msg) override {
        if (strcmp(msg->getName(), "sendTimer") == 0) {
            DroneMessage *dmsg = new DroneMessage();
            dmsg->setSenderId(droneId);
            dmsg->setCreationTime(simTime());

            int ldIndex = droneId % totalLDs;
            char ldName[32];
            sprintf(ldName, "ld[%d]", ldIndex);
            sendDirect(dmsg, getParentModule()->getSubmodule(ldName), "in");
        }
        delete msg;
    }
};

Define_Module(Drone);
