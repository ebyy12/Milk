package com.example.ebymilk

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "Milky.tflite"

    private lateinit var resultText: TextView
    private lateinit var pH: EditText
    private lateinit var Temprature: EditText
    private lateinit var Taste: EditText
    private lateinit var Odor: EditText
    private lateinit var Fat: EditText
    private lateinit var Turbidity: EditText
    private lateinit var Colour: EditText
    private lateinit var checkButton : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        pH = findViewById(R.id.pH)
        Temprature = findViewById(R.id.Temprature)
        Taste = findViewById(R.id.Taste)
        Odor = findViewById(R.id.Odor)
        Fat = findViewById(R.id.Fat)
        Turbidity = findViewById(R.id.Turbidity)
        Colour = findViewById(R.id.Colour)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                pH.text.toString(),
                Temprature.text.toString(),
                Taste.text.toString(),
                Odor.text.toString(),
                Fat.text.toString(),
                Turbidity.text.toString(),
                Colour.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Low"
                }else if (result == 1){
                    resultText.text = "Medium"
                }else if (result == 2){
                    resultText.text = "High"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int{
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(3) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}