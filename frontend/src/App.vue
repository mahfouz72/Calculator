<template>

  <div class="calculator">

    <div class="display-screen">
     <div :class="{hidden: screen ==='0'}">{{screen}}</div>
      <div :class="{hidden : hideResult() || result === '0'}" class="result">
        {{result}}
      </div>
    </div>

    <button @click ="clear()" class="btn clear">C</button>
    <button @click ="clear()" class="btn">CE</button>
    <button @click="appendToScreen('%')" class="btn">%</button>
    <button @click = "backspace()" class="btn">⌫</button>
    <button @click="handleInverse()" class="btn">1/x</button>
    <button @click="handlePower()" class="btn">x²</button>
    <button @click ="appendToScreen('√')" class="btn">√x</button>
    <button @click = "appendToScreen('÷')" class="btn operator">÷</button>
    <button @click = "appendToScreen('7')" class="btn">7</button>
    <button @click = "appendToScreen('8')" class="btn">8</button>
    <button @click = "appendToScreen('9')" class="btn">9</button>
    <button @click = "appendToScreen('×')" class="btn operator">×</button>
    <button @click = "appendToScreen('4')" class="btn">4</button>
    <button @click = "appendToScreen('5')" class="btn">5</button>
    <button @click = "appendToScreen('6')" class="btn">6</button>
    <button @click = "appendToScreen('-')" class="btn operator">-</button>
    <button @click = "appendToScreen('1')" class="btn">1</button>
    <button @click = "appendToScreen('2')" class="btn">2</button>
    <button @click = "appendToScreen('3')" class="btn">3</button>
    <button @click = "appendToScreen('+')" class="btn operator">+</button>
    <button @click = "invertSign()" class="btn">+/-</button>
    <button @click = "appendToScreen('0')" class="btn zero">0</button>
    <button @click = "appendToScreen('.')" class="btn">.</button>
    <button @click = "printFinalResult()" class="btn equal">=</button>

  </div>

</template>
<script>
import axios from 'axios';
export default {
  name: 'App',
  data(){
    return{
      screen:'0',
      decimalPoints :0,
      result:'0',
      processEnded: false,
    }
  },
  mounted() {
    window.addEventListener('keydown', this.handleKeyPress);
  },
  beforeUnmount() {
    window.removeEventListener('keydown', this.handleKeyPress);
  },
  methods:{
    appendToScreen(c){
      if(this.processEnded && (!isNaN(parseInt(c)) || c === '.')){
        this.processEnded = false
        this.clear();
      }

      if(this.isOperator(c)){
        let prev = this.screen.charAt(this.screen.length-1)
        if(prev === '√' || this.screen === '0') return
        this.handleOperator()
      }
      else if(c === '.'){
        if(this.decimalPoints === 1) return;
        this.handleDecimalPoint()
      }
      else if(c === '√'){
        let prev = this.screen.charAt(this.screen.length-1)
        if(prev === '√') return
        this.handleSqrt()
      }
      if(this.screen === '0' && (!isNaN(parseInt(c)) || c === '√')){
        this.screen = c;
      }
      else this.screen += c;

      this.updateResult();
      this.processEnded = false
    },
    handleOperator(){
      let prev = this.screen.charAt(this.screen.length-1)
      this.decimalPoints = 0
      if(this.isOperator(prev) || prev === '.'){
        this.screen = this.screen.substring(0,this.screen.length-1)
      }
    },
    handleDecimalPoint(){
      let prev = this.screen.charAt(this.screen.length-1)
      this.decimalPoints = 1
      if(this.isOperator(prev) || prev === '√')
        this.screen += '0'
    },
    handleInverse(){
      let prev = this.screen.charAt(this.screen.length-1)
      if(prev === '√') return
      if(!this.isOperator(prev)){
        if(prev !== '0' || this.screen.length !== 1)
          this.appendToScreen('×')
      }
      this.appendToScreen('1')
      this.appendToScreen('÷')
    },
    handleSqrt(){
      let prev = this.screen.charAt(this.screen.length-1)
      if(!isNaN(parseInt(prev))){
        if(prev !== '0' || this.screen.length !== 1)
          this.screen += '×'
      }
    },
    invertSign(){
      let lastOperator;
      for(let i = this.screen.length-1; i >= 0 ;i--){
        if(this.isOperator(this.screen.charAt(i))){
          lastOperator = i
          break
        }
      }
      if(this.screen.charAt(lastOperator) === '-' && (this.isOperator(this.screen.charAt(lastOperator-1))||lastOperator === 0)){
        this.screen = this.screen.slice(0,lastOperator) + this.screen.slice(lastOperator+1)
      }
      else if(this.screen.charAt(lastOperator+1) !== '-'){
        this.screen = this.screen.slice(0,lastOperator+1) + '-' + this.screen.slice(lastOperator+1)
      }
      this.updateResult()
    },
    handlePower(){
      let prev = this.screen.charAt(this.screen.length-1)
      if(!this.isOperator(prev) && prev !== '√'){
        this.appendToScreen('^')
        this.appendToScreen('2')
      }
    },
    isOperator(c){
      return c === '+' || c === '-' || c === '÷' || c === '×' || c === '%'
    },
    clear(){
      this.screen = '0'
      this.decimalPoints = 0
      this.result = '0'
    },
    backspace(){
      let lastChar = this.screen.charAt(this.screen.length-1)
      if(lastChar === '.'){
        this.decimalPoints = 0
      }
      else if (this.isOperator(lastChar)){
        this.decimalPoints = this.screen.includes('.') ? 1 : 0
      }

      this.screen = this.screen.slice(0,-1)
      if(this.screen.length === 0) this.screen = '0'
        this.updateResult()
    },
    printFinalResult(){
      let lastChar = this.screen.charAt(this.screen.length-1)
      if(this.result === '0' || this.result ==='E' || isNaN(parseInt(lastChar)))
          return

      this.screen = this.result.toString()
      this.result = '0'
      this.processEnded = true
    },
    updateResult(){
      let lastChar = this.screen.charAt(this.screen.length-1)
      if(isNaN(parseInt(lastChar))) return

      axios.get(`/api/evaluate?exp=${encodeURIComponent(this.screen)}`)
          .then(response => {
            if(response.data !== '-Infinity' && response.data !== 'Infinity' )
              this.result = response.data
            else this.result = 'E'
          })
          .catch(error => console.log(error))
    },
    hideResult(){
      let lastChar = this.screen.charAt(this.screen.length-1)
      return isNaN(parseInt(lastChar))
    },
    handleKeyPress(event){
      const key = event.key;
      switch (key) {
        case 'Enter':
          this.printFinalResult();
          break;
        case 'Backspace':
          this.backspace();
          break;
        case '*':
          this.appendToScreen('×');
          break;
        case '/':
          this.appendToScreen('÷');
          break;
        case '.':
          this.appendToScreen('.');
          break;
        default:
          if (!isNaN(parseInt(key))) {
            this.appendToScreen(key);
          } else if (this.isOperator(key)) {
            this.appendToScreen(key);
          }
          break;
      }
    }

  }
}
</script>
<style>
body{
  background-color: #252b32;
}
.calculator{
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  width: 300px;
  margin: 100px auto;
  background-color: black;
  border-radius: 20px;
  border: 2px solid grey;
  box-shadow: 5px 5px 10px rgba(0, 0, 0, 0.1);
}
.display-screen{
  grid-column: 1/5;
  padding: 20px;
  color: white;
  font-size: 28px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 40px;
  border-bottom: 1px solid grey;
  margin-bottom: 10px;
}
.btn{
  height: 60px;
  width: 60px;
  margin: 3px 3px 3px 7px;
  background-color: #171717;
  border-radius: 100%;
  color: white;
  font-size: 18px;
  border: none;
  cursor: pointer;
  font-weight: bold;
}
.operator{
  background-color: #323232;
  color: #318607;
  font-size: 30px;
}
.equal{
  background-color: #318607;
  color: white;
  font-size: 30px;
}
.btn:hover{
  opacity: 0.7;
}
.clear{
  color: red;
}
.result{
  color: grey;
  font-size: 22px;
}
.hidden{
  visibility: hidden;
}
</style>
