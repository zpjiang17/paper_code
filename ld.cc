#include <omnetpp.h>
#include "DroneMessage_m.h"

using namespace omnetpp;

class LD : public cSimpleModule
{
  protected:
    virtual void handleMessage(cMessage *msg) override {
        if (DroneMessage *dmsg = dynamic_cast<DroneMessage *>(msg)) {
                       scheduleAt(simTime() + uniform(0.2, 0.5), dmsg);
        } else {
                      sendDirect(msg, getParentModule()->getSubmodule("gl"), "in");
        }
    }
};

Define_Module(LD);
