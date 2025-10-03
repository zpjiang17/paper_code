#include <omnetpp.h>
#include "DroneMessage_m.h"

using namespace omnetpp;

class GL : public cSimpleModule
{
  protected:
    virtual void handleMessage(cMessage *msg) override {
        if (DroneMessage *dmsg = dynamic_cast<DroneMessage *>(msg)) {
                        scheduleAt(simTime() + uniform(0.2, 0.5), dmsg);
        } else {
            sendDirect(msg, getParentModule()->getSubmodule("gcs"), "in");
        }
    }
};

Define_Module(GL);
