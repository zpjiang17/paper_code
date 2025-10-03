#include <omnetpp.h>
#include "DroneMessage_m.h"

using namespace omnetpp;

class GCS : public cSimpleModule
{
  private:
    simsignal_t latencySignal;
    int received = 0;
    int expected = 0;

  protected:
    virtual void initialize() override {
        latencySignal = registerSignal("latency");
        expected = par("numDrones").intValue();
    }

    virtual void handleMessage(cMessage *msg) override {
        DroneMessage *dmsg = check_and_cast<DroneMessage *>(msg);
        simtime_t delay = simTime() - dmsg->getCreationTime();
        emit(latencySignal, delay.dbl());
        received++;
        delete msg;
    }

    virtual void finish() override {
        double lossRate = 100.0 * (expected - received) / expected;
        recordScalar("packetLossRate(%)", lossRate);
    }
};

Define_Module(GCS);
