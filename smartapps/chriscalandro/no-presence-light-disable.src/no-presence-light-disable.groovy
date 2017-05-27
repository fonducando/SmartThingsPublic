/**
 *  Livingroom motion lightoff
 *
 *  Copyright 2016 Chris Calandro
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "no presence light disable",
    namespace: "chriscalandro",
    author: "Chris Calandro",
    description: "disable lights if nobody is home",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Which lights do you want to cancel automation on?") {
	input "lights", "capability.switch", multiple:true, required:true	
	}    
    section("Who needs to be home to enable lights on?"){
    input "presencesensor", "capability.presenceSensor", multiple:true, required:true
    }
    section("How many people live in your house or have presence sensors?"){
    input "numberofmembers", "number", required:true
    }
    section("how many people are home right now as you are programming this?"){
    input "currentlyhome", "number", required:true
    }
 
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(motionSensor, "motion", motionHandler)
    subscribe(presencesensor, "presence", presHandler)
    subscribe(lights, "switch", switchHandler)
    state.someoneishome = currentlyhome
    log.debug (currentlyhome)
}

def presHandler(evt) {
  if("present" == evt.value){
    state.someoneishome = (state.someoneishome + 1)
       if (state.someoneishome > numberofmembers){state.someoneishome = numberofmembers}
    log.debug (state.someoneishome)
                            }
  else {state.someoneishome = (state.someoneishome - 1)
      if (state.someoneishome < 0) {state.someoneishome = 0}
  log.debug (state.someoneishome)
       }
                         }

def motionHandler(evt){
  if("active" == evt.value){
    state.someoneismoving = "true"
                          }
  else {state.someoneismoving = "false"}
                      }
                         
def switchHandler(evt){
    if ("on" == evt.value){
     if (state.someoneishome >=1){
       log.debug "Leaving Lights on while people are home"
                                 }
     if (state.someoneishome == 0) {
     lights.off()
     log.debug "Nobody is home, turning lights back off"
                                  }
                        }
                      }