#include <Servo.h>

int SERVO_PWM_PIN = 6;
int LED_PIN = 7;
int BTN0_PIN = 8;
int BTN1_PIN = 9;
int BTN_SYNC_PIN = 10;

int SERVO_LOCK_ANGLE = -90;
int SERVO_UNLOCK_ANGLE = 0;

int SEED = 1234;
int PERIOD_MS = 15000;

int code = -1;
int code_arr[10];

int inputIndex = 0;

bool lastPress0 = false;
bool lastPress1 = false;
bool lastPressSync = false;
bool isUnlocked = false;

Servo servo;

int lastTime_ms = -1;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  pinMode(BTN0_PIN, INPUT);
  pinMode(BTN1_PIN, INPUT);
  pinMode(BTN_SYNC_PIN, INPUT);

  digitalWrite(LED_PIN, LOW);

  servo.attach(SERVO_PWM_PIN);
  servo.write(SERVO_LOCK_ANGLE);
  
  code = SEED;
}

void loop() {
  if(millis() - lastTime_ms > PERIOD_MS) {
    getNextCode(); 
    lastTime_ms = millis();
  }

  if(isUnlocked) {
    if(btn0Pressed() || btn1Pressed()) {
      // relock
      inputIndex = 0;
      servo.write(SERVO_LOCK_ANGLE);
    }
    return;
  }

  bool btn0 = btn0Pressed();
  bool btn1 = btn1Pressed();
  int expected = code_arr[inputIndex];

  if((btn0 && expected == 0) || (btn1 && expected == 1)) {
    inputIndex++;
  }
  else if((btn0 && expected == 1) || (btn1 && expected == 0)) {
    inputIndex = 0;
  }

  if(inputIndex == 10) {
    isUnlocked = true;
    // unlock
    servo.write(SERVO_UNLOCK_ANGLE);
    digitalWrite(LED_PIN, HIGH);
  }
}

bool btn0Pressed() {
  bool btnState = digitalRead(BTN0_PIN) == HIGH;
  if(!lastPress0 && btnState) {
    lastPress0 = btnState;
    return true;
  }
  else {
    lastPress0 = btnState;
    return false;
  }
}

bool btn1Pressed() {
  bool btnState = digitalRead(BTN1_PIN) == HIGH;
  if(!lastPress1 && btnState) {
    lastPress1 = btnState;
    return true;
  }
  else {
    lastPress1 = btnState;
    return false;
  }
}

bool btnSyncPressed() {
  bool btnState = digitalRead(BTN_SYNC_PIN) == HIGH;
  if(!lastPressSync && btnState) {
    lastPressSync = btnState;
    return true;
  }
  else {
    lastPressSync = btnState;
    return false;
  }
}

void getNextCode() {
  int a = 3;
  int c = 2555;
  int m = 1024;

  code = (a * code + c) % m;

  int i = 0;
  while (i < 10) {
    if ((code >> i) & 1)
      code_arr[i] = 1;
    else
      code_arr[i] = 0;
    i++;
  }
}
