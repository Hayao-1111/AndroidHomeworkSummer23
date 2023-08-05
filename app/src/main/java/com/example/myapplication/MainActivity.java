package com.example.myapplication;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapplication.model.PlaceStatusEnum.SPACE;
import static com.example.myapplication.model.PlaceStatusEnum.BLOCKED;
import static com.example.myapplication.model.PlaceStatusEnum.PEG;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.Toast;

import com.example.myapplication.model.PlaceStatusEnum;
import com.example.myapplication.model.SpacePosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    private static final int TEXT_COLOR_BROWN = 0xffa52a2a;
    private static final int TEXT_COLOR_RED = 0xffff0000;

    /**
     * Unicode字符：实心方块
     */
    private static final String TOKEN_MARK = "■";

    /**
     * 用于存储棋盘初始化的数组。
     */
    private static final PlaceStatusEnum[][] PLACE_INIT_ARRAY =
            {
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {PEG, PEG, PEG, SPACE, PEG, PEG, PEG},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED}
            };

    private final int _sizeColumn = PLACE_INIT_ARRAY.length;

    private final int _sizeRow = PLACE_INIT_ARRAY[0].length;

    /**
     * 用于存储棋盘上的棋子和空位置的数组。
     */
    private PlaceStatusEnum[][] _placeArray = null;

    /**
     * 当前棋盘上的棋子数量。
     */
    private int _numberOfPegs = -1;
    /**
     * 当前执行的步数。
     */
    private int _numberOfSteps = -1;
    /**
     * 选中的棋子是否已经被移动了。
     */
    private boolean _selectedPegMoved = false;

    /**
     * 用于存储棋盘上的棋子的按钮。
     */
    private ViewGroup.LayoutParams _buttonLayoutParams = null;

    /**
     * 用于开始新游戏的按钮。
     */
    private Button _startButton = null;

    /**
     * 棋盘上的棋子和空位置的布局。
     */
    private GridLayout _gridLayout = null;


    /**
     * 用于处理点击棋盘上的棋子的事件。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "column=" + _sizeColumn + ", row=" + _sizeRow + "px:");

        _gridLayout = findViewById(R.id.boardGridLayout);

        displayResolutionEvaluate();
        actionBarConfiguration();
        initializeBoard();
    }

    /**
     * 从显示器读取分辨率并将值写入适当的成员变量。
     */
    private void displayResolutionEvaluate() {

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        Log.i(TAG4LOGGING, "Display-Resolution: " + displayWidth + "x" + displayHeight);

        int _sideLengthPlace = displayWidth / _sizeColumn;

        _buttonLayoutParams = new ViewGroup.LayoutParams(_sideLengthPlace,
                _sideLengthPlace);
    }

    /**
     * 初始化操作栏。
     */
    private void actionBarConfiguration() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "没有操作栏", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle("单人跳棋");
    }

    /**
     * 从资源文件加载操作栏菜单项。
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 处理操作栏菜单项的选择。
     * 在扩展的版本中，你需要加入更多的菜单项。
     *
     * @param item 选择的菜单项
     * @return true: 选择的菜单项被处理了
     * false: 选择的菜单项没有被处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_new_game) {

            selectedNewGame();
            return true;

        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * 处理点击"新游戏"按钮的事件。
     * 弹出对话框，询问用户是否要开始新游戏。
     * 如果用户选择"是"，则初始化棋盘，否则不做任何事情。
     */
    public void selectedNewGame() {
        /* TODO // Done */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新游戏")
                .setMessage("是否开始新游戏？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 处理“是”的点击事件
                        initializeBoard();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 处理“否”的点击事件
                        // 不做任何处理
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * 初始化棋盘上的棋子和空位置。
     */
    private void initializeBoard() {

        if (_gridLayout.getRowCount() == 0) {

            _gridLayout.setColumnCount(_sizeRow);

        } else { // 清除旧的棋盘

            _gridLayout.removeAllViews();
        }

        _numberOfSteps = 0;
        _numberOfPegs = 0;
        _selectedPegMoved = false;
        _placeArray = new PlaceStatusEnum[_sizeColumn][_sizeRow];

        for (int i = 0; i < _sizeColumn; i++) {

            for (int j = 0; j < _sizeRow; j++) {

                PlaceStatusEnum placeStatus = PLACE_INIT_ARRAY[i][j];

                _placeArray[i][j] = placeStatus;

                switch (placeStatus) {

                    case PEG:
                        generateButton(i, j, true);
                        break;

                    case SPACE:
                        generateButton(i, j, false);
                        break;

                    case BLOCKED:
                        Space space = new Space(this); // Dummy-Element
                        _gridLayout.addView(space);
                        break;

                    default:
                        Log.e(TAG4LOGGING, "错误的棋盘状态");

                }
            }
        }

        Log.i(TAG4LOGGING, "棋盘初始化完成");
        updateDisplayStepsNumber();
    }

    /**
     * 生成棋盘上的一个位置。
     * 在基础任务中，棋盘上的棋子直接用字符 TOKEN_MARK 表示。
     * 在扩展任务中，棋盘上的棋子用图片表示。
     */
    private void generateButton(int indexColumn, int indexRow, boolean isPeg) {

        Button button = new Button(this);

        button.setTextSize(22.0f);
        button.setLayoutParams(_buttonLayoutParams);
        button.setOnClickListener(this);
        button.setTextColor(TEXT_COLOR_BROWN);

        SpacePosition pos = new SpacePosition(indexColumn, indexRow);
        button.setTag(pos);

        /* TODO // Done */
        if (isPeg)  // 如果有棋子，则显示字符TOKEN_MARK
        {
            button.setText(TOKEN_MARK);
        }
        else        // 如果无棋子，则显示空白
        {
            button.setText(" ");
        }

        // 将当前位置的按钮布局添加到整体布局中
        _gridLayout.addView(button);
    }


    /**
     * 更新操作栏中的步数显示。
     */
    private void updateDisplayStepsNumber() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle("执行步数：" + _numberOfSteps);
        }
    }

    /**
     * 处理棋盘上的点击事件。
     * 如果被点击的按钮是一个棋子，那么它将被改变选中状态。
     * 也就是说，如果它之前没有被选中，这个棋子会变为红色，
     * 同时，此前被选中的棋子（如果有）将变为棕色。
     * 或者如果它已经被选中，那么它自己将变为棕色。
     * 如果被点击的按钮是一个空位置，那么试图将被选中的棋子移动到该位置。
     * 如果移动成功，你需要更新棋盘上的棋子和空位置。
     * 如果移动失败，你需要显示一个错误信息。
     *
     * @param view 被点击的按钮
     *

     */
    @Override
    public void onClick(View view) {

        Button clickedButton = (Button) view;

        SpacePosition targetPosition = (SpacePosition) clickedButton.getTag();

        // 获取被点击的按钮的位置
        int indexColumn = targetPosition.getIndexColumn();
        int indexRow = targetPosition.getIndexRow();
        PlaceStatusEnum placeStatus = _placeArray[indexColumn][indexRow];

        switch (placeStatus) {

            case PEG:
                // TODO // Done
                // 未被选中的棋子
                //  1. 选中的棋子变成红色
                clickedButton.setTextColor(TEXT_COLOR_RED);
                //  2. 此前选中的棋子（如果有）变成棕色，并标记为未选中
                for (int i = 0; i < _sizeColumn; i++) {
                    for (int j = 0; j < _sizeRow; j++) {
                        // 判断数组元素是否为 PEG_SELECTED
                        if (_placeArray[i][j] == PlaceStatusEnum.PEG_SELECTED) {
                            // 将 PEG_SELECTED 改为 PEG
                            _placeArray[i][j] = PlaceStatusEnum.PEG;
                            // 更改对应位置的棋子的颜色
                            SpacePosition the_pos = new SpacePosition(i,j);
                            Button the_btn = getButtonFromPosition(the_pos);
                            the_btn.setTextColor(TEXT_COLOR_BROWN);
                        }
                    }
                }
                //  3. 选中的棋子标记为选中
                _placeArray[indexColumn][indexRow] = PlaceStatusEnum.PEG_SELECTED;

                break;

            case PEG_SELECTED:
                // TODO // Done
                // 之前已经被选中的棋子
                //  1. 将棋子颜色由红色变成棕色
                clickedButton.setTextColor(TEXT_COLOR_BROWN);
                //  2. 将棋子的标记改为未选中
                _placeArray[indexColumn][indexRow] = PlaceStatusEnum.PEG;

                break;

            case SPACE:
                // TODO // Done
                // 如果被点击的按钮是一个空位置
                //  1. 找到被选中的棋子，如果之前没有选中的棋子，则返回错误信息
                boolean if_exist_selected = false;
                Button selected_btn = null;
                SpacePosition the_pos = null;
                for (int i = 0; i < _sizeColumn; i++) {
                    for (int j = 0; j < _sizeRow; j++) {
                        if (_placeArray[i][j] == PlaceStatusEnum.PEG_SELECTED)
                        {
                            if_exist_selected = true;
                            the_pos = new SpacePosition(i,j);
                            selected_btn = getButtonFromPosition(the_pos);
                        }
                    }
                }

                if (!if_exist_selected)
                {
                    Log.e(TAG4LOGGING, "不存在选中的棋子");
                    break;
                }

                //  2. 试图将被选中的棋子移动到该位置
                //  2.1 判断是否合法
                //  2.1.1 获得目标位置（即当前位置）及其对应的按钮对象 //
                SpacePosition target_position = new SpacePosition(indexColumn, indexRow);
                Button target_button = getButtonFromPosition(target_position);

                // 2.1.2 获得初始位置及其对应的按钮对象
                // 已存在 初始位置 `the_pos`, 对应的按钮对象 `selected_btn`

                //  2.1.3 获得被跳过的位置及其对应的按钮对象
                SpacePosition skipped_pos =  getSkippedPosition(the_pos, target_position);

                if (skipped_pos == null)
                {
                    Log.e(TAG4LOGGING, "不能跳过, 请重新选择合法的空位置");
                    break;
                }

                Button skipped_button = getButtonFromPosition(skipped_pos);

                //  2.2 在合法的前提下，移动棋子
                jumpToPosition(selected_btn, target_button, skipped_button);


                break;

            default:
                Log.e(TAG4LOGGING, "错误的棋盘状态" + placeStatus);
        }
    }


    /**
     * 新增一个函数，将按钮所对应的位置的文本及状态进行改变
     *
     * @param theButton Button 选中的按钮
     * @param status PlaceStatusEnum 新的状态
     * @param ifChangeText boolean 是否需要修改文本
     * @param newText String 新的文本
     * @param ifChangeColor boolean 是否需要修改颜色
     * @param newColor int 新的颜色
     */
    private void changeStatusOfButton(Button theButton, PlaceStatusEnum status,
                                      boolean ifChangeText, String newText,
                                      boolean ifChangeColor, int newColor)
    {
        // 获取被点击的按钮的位置
        SpacePosition thePosition = (SpacePosition) theButton.getTag();
        int indexColumn = thePosition.getIndexColumn();
        int indexRow = thePosition.getIndexRow();

        // 改变状态
        _placeArray[indexColumn][indexRow] = status;

        // 改变文本（如果需要）
        if (ifChangeText) {
            theButton.setText(newText);
        }

        // 改变颜色（如果需要）
        if (ifChangeColor){
            theButton.setTextColor(newColor);
        }

    }


    /**
     * 执行跳跃。仅当确定移动合法时才可以调用该方法。
     * 数组中三个位置的状态，和总棋子数发生变化。
     * 同时，在移动后，你需要检查是否已经结束游戏。
     *
     * @param startButton 被选中的棋子
     * @param targetButton 被选中的空位置
     * @param skippedButton 被跳过的棋子
     *
     */
    private void jumpToPosition(Button startButton, Button targetButton, Button skippedButton) {

        // TODO // Done

        // 数组中三个位置的状态变化
        // (1) 起始按钮成为空位置，状态变成SPACE，文本变成空白
        changeStatusOfButton(startButton, SPACE, true, " ",
                true, TEXT_COLOR_BROWN);
        // (2) 目标按钮状态变成PEG，文本不变
        changeStatusOfButton(targetButton, PEG, true, TOKEN_MARK,
                true, TEXT_COLOR_BROWN);
        // (3) 被跳过的按钮成为空位置，状态变成SPACE，文本变成空白
        changeStatusOfButton(skippedButton, SPACE, true, " ",
                false, -1);

        // 步数发生变化
        _numberOfSteps++;

        // 总棋子数发生变化
        _numberOfPegs--;
        updateDisplayStepsNumber();

        // 在移动后，检查是否已经结束游戏
        if (_numberOfPegs == 1) {   // 最终只剩下一个棋子在棋盘上，则取得胜利
            showVictoryDialog();
        } else if (!has_movable_places()) { // 若剩余棋子数多于1且没有可落脚的位置，则失败
            showFailureDialog();
        }
    }

    /**
     * 返回位置对应的按钮。
     *
     * @param position 位置
     * @return 按钮
     */
    private Button getButtonFromPosition(SpacePosition position) {

        int index = position.getPlaceIndex(_sizeRow);

        return (Button) _gridLayout.getChildAt(index);
    }

    /**
     * 显示一个对话框，表明游戏已经胜利（只剩下一个棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     * 在扩展版本中，你需要在这里添加一个输入框，让用户输入他的名字。
     */
    private void showVictoryDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("胜利");
        dialogBuilder.setMessage("你赢了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 显示一个对话框，表明游戏已经失败（没有可移动的棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     */
    private void showFailureDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("失败");
        dialogBuilder.setMessage("你输了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 给定一个起始位置和目标位置。
     * 如果移动合法，返回被跳过的位置。
     * 如果移动不合法，返回 {@code null}。
     * 移动合法的定义，参见作业文档。
     *
     * @param startPos  起始位置
     * @param targetPos 目标位置
     * @return 移动合法时，返回一个新{@code SpacePosition}
     * 表示被跳过的位置；否则返回 {@code null}
     */
    private SpacePosition getSkippedPosition(SpacePosition startPos, SpacePosition targetPos) {
        // TODO
        int start_indexColumn = startPos.getIndexColumn();
        int start_indexRow = startPos.getIndexRow();
        int target_indexColumn = targetPos.getIndexColumn();
        int target_indexRow = targetPos.getIndexRow();

        int skipped_row = 0;
        int skipped_column = 0;

        if (start_indexColumn == target_indexColumn && Math.abs(start_indexRow - target_indexRow) == 2)
        {
            skipped_column = start_indexColumn;
            skipped_row = (start_indexRow + target_indexRow) / 2;
            SpacePosition skipped_pos = new SpacePosition(skipped_column, skipped_row);
            return skipped_pos;
        }
        else if (start_indexRow == target_indexRow && Math.abs(start_indexColumn - target_indexColumn) == 2)
        {
            skipped_row = start_indexRow;
            skipped_column = (start_indexColumn + target_indexColumn) / 2;
            SpacePosition skipped_pos = new SpacePosition(skipped_column, skipped_row);
            return skipped_pos;
        }
        else
        {
            return null;
        }

    }

    /**
     * 返回是否还有可移动的位置。
     *
     * @return 如果还有可移动的位置，返回 {@code true}
     * 否则返回 {@code false}
     */
    private Boolean has_movable_places(){
        for(int i = 0; i < _sizeColumn; i++){
            for(int j = 0; j < _sizeRow; j++){
                if(_placeArray[i][j] == PEG){
                    // TODO // Done

                    if (i >= 2 && _placeArray[i - 1][j] == PEG && _placeArray[i - 2][j] == SPACE) {
                        return true;
                    }
                    if (i <= _sizeColumn - 3 && _placeArray[i + 1][j] == PEG && _placeArray[i + 2][j] == SPACE) {
                        return true;
                    }
                    if (j >= 2 && _placeArray[i][j - 1] == PEG && _placeArray[i][j - 2] == SPACE) {
                        return true;
                    }
                    if (j <= _sizeRow - 3 && _placeArray[i][j + 1] == PEG && _placeArray[i][j + 2] == SPACE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
