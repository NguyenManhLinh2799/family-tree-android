package me.jagar.mindmappingandroidlibrary.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import me.jagar.mindmappingandroidlibrary.Listeners.OnItemClicked;

public class MindMappingView extends RelativeLayout {

    private static final int LEVEL_SPACING = 400;
    private static final int PARENT_SPACING = 220;
    private static final int MIN_SPACING = 220;
    private float centralPointX;
    private float centralPointY;
    private Item root;
    private int height;

    private Context context;
    private Activity activity;
    private ArrayList<Connection> topItems = new ArrayList<>();
    private ArrayList<Connection> leftItems = new ArrayList<>();
    private ArrayList<Connection> rightItems = new ArrayList<>();
    private ArrayList<Connection> bottomItems = new ArrayList<>();
    private ArrayList<CustomConnection> customConnections = new ArrayList<>();
    private int connectionWidth = 10, connectionArrowSize = 5, connectionCircRadius = 5, connectionArgSize = 30;
    private String connectionColor = "#000000";
    private MindMappingView mindMappingView;
    private OnItemClicked onItemClicked;


    public MindMappingView(Context context) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        mindMappingView = this;
    }

    public MindMappingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activity = (Activity) context;
        mindMappingView = this;
    }

    public MindMappingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.activity = (Activity) context;
        mindMappingView = this;
    }

    //Adding the root item
    @SuppressLint("ClickableViewAccessibility")
    public void addCentralItem(Item item){

        root = item;

        item.setGravity(CENTER_IN_PARENT);
        this.setGravity(Gravity.CENTER);

        boolean dragAble = false;
        if (dragAble){
            dragItem(item);
        }

        centralPointX = item.getX();
        centralPointY = item.getY();
        
        this.addView(item);
    }

    public void setRoot(Item item) {
        this.root = item;
    }

    /*Make any item drag able, This will make issues with
    a simple call of OnClickListener on the Item objects so you set it off to call the normal onclicklistener
    the custom OnItemClicked*/
    @SuppressLint("ClickableViewAccessibility")
    private void dragItem(final Item item) {
        final float[] dX = new float[1];
        final float[] dY = new float[1];

        item.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dX[0] = view.getX() - motionEvent.getRawX();
                        dY[0] = view.getY() - motionEvent.getRawY();
                        if (onItemClicked != null)
                            onItemClicked.OnClick(item);
                        break;
                    case MotionEvent.ACTION_MOVE:
                            view.animate()
                                    .x(motionEvent.getRawX() + dX[0])
                                    .y(motionEvent.getRawY() + dY[0])
                                    .setDuration(0)
                                    .start();
                            invalidate();

                        break;
                    default:
                        item.setPressed(false);
                        return false;
                }
                return true;
            }
        });

    }

    //Adding an item that has the parent already on the view
    public void addItem(Item newItem, Item base, int location){

        if (newItem.type == ItemType.FAMILY) {
            newItem.setZ(-10);
        }

        if (location == ItemLocation.TOP){
            this.addView(newItem);

            root = newItem;
            newItem.Children.add(base);
            base.Parent = newItem;

            Connection connection = new Connection(newItem, base, null);
            topItems.add(connection);
            newItem.addParent(base, ItemLocation.TOP);
            newItem.addConnection(base, ItemLocation.TOP, null);

        } else if (location == ItemLocation.LEFT){
            this.addView(newItem);

            if (newItem.type == ItemType.FAMILY) {
                newItem.Wife = base;
                base.Family = newItem;
            } else if (base.type == ItemType.FAMILY) {
                newItem.Family = base;
                if (base.Wife != null) {
                    newItem.Partner = base.Wife;
                    base.Wife.Partner = newItem;
                }
                base.Husband = newItem;
            } else {
                newItem.Partner = base;
            }

            Connection connection = new Connection(newItem, base);
            leftItems.add(connection);
            newItem.addParent(base, ItemLocation.LEFT);
            newItem.addConnection(base, ItemLocation.LEFT, null);

        } else if (location == ItemLocation.RIGHT){
            this.addView(newItem);

            if (newItem.type == ItemType.FAMILY) {
                newItem.Husband = base;
                base.Family = newItem;
            } else if (base.type == ItemType.FAMILY) {
                newItem.Family = base;
                if (base.Husband != null) {
                    newItem.Partner = base.Husband;
                    base.Husband.Partner = newItem;
                }
                base.Wife = newItem;
            } else {
                newItem.Partner = base;
            }

            Connection connection = new Connection(newItem, base);
            rightItems.add(connection);
            newItem.addParent(base, ItemLocation.RIGHT);
            newItem.addConnection(base, ItemLocation.RIGHT, null);

        } else if (location == ItemLocation.BOTTOM){
            this.addView(newItem);

            newItem.Parent = base;
            base.Children.add(newItem);

            Connection connection = new Connection(newItem, base);
            bottomItems.add(connection);
            newItem.addParent(base, ItemLocation.BOTTOM);
            newItem.addConnection(base, ItemLocation.BOTTOM, null);
        }

        boolean dragAble = false;
        if (dragAble){
            dragItem(newItem);
        }
    }

    // Reingold-Tilford algorithm
    public void ReingoldTilford() {
        if (root == null) {
            return;
        }

        completeTree(root);
        initializeNodes(root, 0);
        calculateInitialX(root);
        calculateFinalPositions(root, 0);
        this.height = getTreeHeight(root);
        calculateCoordinates(root);
        spacingFamily(root);

        root.Husband.setX(root.getX() - (float) PARENT_SPACING / 2);
        root.Husband.setY(root.getY());
        root.Wife.setX(root.getX() + (float) PARENT_SPACING / 2);
        root.Wife.setY(root.getY());
    }

    private void completeTree(Item node) {

        for (int i = 0; i < node.Children.size(); i++) {
            Item child = node.Children.get(i);
            if (child.Partner != null) {
                if (child.type == ItemType.MALE) {
                    node.Children.add(node.Children.indexOf(child) + 1, child.Partner);
                } else if (child.type == ItemType.FEMALE) {
                    node.Children.add(node.Children.indexOf(child), child.Partner);
                }
                child.Partner.Parent = node;
                i++;
            }
            if (child.Family != null) {
                if (child.type == ItemType.MALE) {
                    node.Children.add(node.Children.indexOf(child) + 1, child.Family);
                } else if (child.type == ItemType.FEMALE) {
                    node.Children.add(node.Children.indexOf(child), child.Family);
                }
                child.Family.Parent = node;
                i++;
            }
        }

        for (Item child : node.Children) {
            completeTree(child);
        }
    }

    private void preOrder(Item root) {
        if (root.type == ItemType.FAMILY) {
            Log.e("MindMappingView", "family (" + root.X + ", " + root.Y + ")");
        } else {
            Log.e("MindMappingView", root.getTitle().getText().toString() + " (" + root.X + ", " + root.Y + ")");
        }

        for (int i = 0; i < root.Children.size(); i++) {
            preOrder(root.Children.get(i));
        }
    }

    private void postOrder(Item root) {
        for (int i = 0; i < root.Children.size(); i++) {
            postOrder(root.Children.get(i));
        }

        if (root.type == ItemType.FAMILY) {
            Log.e("MindMappingView", "family (" + root.X + ", " + root.Y + ")");
        } else {
            Log.e("MindMappingView", root.getTitle().getText().toString() + " (" + root.X + ", " + root.Y + ")");
        }
    }

    private void initializeNodes(Item node, int depth) {
        node.X = -1;
        node.Y = depth;
        node.Mod = 0;

        for (Item child : node.Children) {
            initializeNodes(child, depth + 1);
        }
    }

    private void calculateInitialX(Item node) {
        for (int i = 0; i < node.Children.size(); i++) {
            calculateInitialX(node.Children.get(i));
        }

        if (node.isLeaf()) {
            if (!node.isLeftMost()) {
                node.X = node.getPreviousSibling().X + 1;
            } else {
                node.X = 0;
            }
        } else if (node.Children.size() == 1) {
            if (node.isLeftMost()) {
                node.X = node.Children.get(0).X;
            } else {
                node.X = node.getPreviousSibling().X + 1;
                node.Mod = node.X - node.Children.get(0).X;
            }
        } else {
            Item leftMost = node.getLeftMostChild();
            Item rightMost = node.getRightMostChild();
            float mid = (leftMost.X + rightMost.X) / 2;
            if (node.isLeftMost()) {
                node.X = mid;
            } else {
                node.X = node.getPreviousSibling().X + 1;
                node.Mod = node.X - mid;
            }
        }

        if (node.Children.size() > 0 && !node.isLeftMost()) {
            checkForConflicts(node);
        }
    }

    private void checkForConflicts(Item node) {
        float minDistance = 1f;
        float shiftValue = 0;

        HashMap<Integer, Float> nodeContour = new HashMap<>();
        getLeftContour(node, 0, nodeContour);

        Item sibling = node.getLeftMostSibling();
        while (sibling != null && sibling != node) {
            HashMap<Integer, Float> siblingContour = new HashMap<>();
            getRightContour(node, 0, siblingContour);
            for (int level = node.Y + 1; level <= Math.min(Collections.max(siblingContour.keySet()), Collections.max(siblingContour.keySet())); level++) {
                float distance = nodeContour.get(level) - siblingContour.get(level);
                if (distance + shiftValue < minDistance) {
                    shiftValue = minDistance - distance;
                }
            }
            if (shiftValue > 0) {
                node.X += shiftValue;
                node.Mod += shiftValue;
                centerNodesBetween(sibling, node);
                shiftValue = 0;
            }
            sibling = sibling.getNextSibling();
        }
    }

    private void centerNodesBetween(Item leftNode, Item rightNode) {
        int leftIndex = leftNode.Parent.Children.indexOf(leftNode);
        int rightIndex = leftNode.Parent.Children.indexOf(rightNode);

        int countNodesBetween = (rightIndex - leftIndex) - 1;
        if (countNodesBetween > 0) {
            float distanceBetweenNodes = (rightNode.X - leftNode.X) / (countNodesBetween + 1);
            int count = 1;
            for (int i = leftIndex + 1; i < rightIndex; i++) {
                Item middleNode = leftNode.Parent.Children.get(i);
                float desiredX = leftNode.X + (distanceBetweenNodes * count);
                float offset = desiredX - middleNode.X;
                middleNode.X += offset;
                middleNode.Mod += offset;
                count++;
            }
            checkForConflicts(leftNode);
        }
    }

    private void getLeftContour(Item node, float modSum, HashMap<Integer, Float> values) {
        if (!values.containsKey(node.Y)) {
            values.put(node.Y, node.X + modSum);
        } else {
            values.put(node.Y, Math.min(values.get(node.Y), node.X + modSum));
        }
        modSum += node.Mod;
        for (Item child : node.Children) {
            getLeftContour(child, modSum, values);
        }
    }

    private void getRightContour(Item node, float modSum, HashMap<Integer, Float> values) {
        if (!values.containsKey(node.Y)) {
            values.put(node.Y, node.X + modSum);
        } else {
            values.put(node.Y, Math.max(values.get(node.Y), node.X + modSum));
        }
        modSum += node.Mod;
        for (Item child : node.Children) {
            getLeftContour(child, modSum, values);
        }
    }

    private void calculateFinalPositions(Item node, float modSum) {
        node.X += modSum;
        modSum += node.Mod;
        for (Item child : node.Children) {
            calculateFinalPositions(child, modSum);
        }
    }

    private void calculateCoordinates(Item node) {
        float distanceFromCentralY = Math.abs(node.Y - this.height / 2);
        if (node.Y < this.height / 2) {
            node.setY(centralPointY - distanceFromCentralY * LEVEL_SPACING);
        } else {
            node.setY(centralPointY + distanceFromCentralY * LEVEL_SPACING);
        }

        float distanceFromCentralX = Math.abs(node.X - root.X);
        if (node.X < root.X) {
            node.setX(centralPointX - distanceFromCentralX * MIN_SPACING);
        } else {
            node.setX(centralPointX + distanceFromCentralX * MIN_SPACING);
        }

        for (Item child : node.Children) {
            calculateCoordinates(child);
        }
    }

    private void spacingFamily(Item node) {
        if (node.type == ItemType.FAMILY) {
            node.Husband.setX(node.getX() - (float) PARENT_SPACING / 2);
            node.Husband.setY(node.getY());
            node.Wife.setX(node.getX() + (float) PARENT_SPACING / 2);
            node.Wife.setY(node.getY());
        }

        for (Item child : node.Children) {
            spacingFamily(child);
        }
    }

    private int getTreeHeight(Item node) {
        int max = 0;
        for (Item child : node.Children) {
            int childHeight = getTreeHeight(child);
            if (childHeight > max) {
                max = childHeight;
            }
        }
        return 1 + max;
    }

    //Draw connections

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTopLines(canvas);
        drawLeftLines(canvas);
        drawRightLines(canvas);
        drawBottomLines(canvas);
        drawCustomConnection(canvas);
    }

    //Draw connections (default)
    private void drawTopLines(Canvas canvas) {

        for (Connection connection : topItems){
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth()/2);
            int y1 = (int) (parent.getY() + parent.getHeight()/2);
            int x2 = (int) (item.getX() + item.getWidth()/2);
            int y2 = (int) (item.getY() + item.getHeight()/2);
            //int radius = (int) (((item.getY() + item.getHeight()) - (parent.getY()))/4);
            int radius = 0;
            drawCurvedArrowTop(x1, y1, x2, y2, radius, canvas,
                    item.getX() > parent.getX(), item.getConnectionByParent(parent).getConnectionTextMessage(), connection);

            System.out.println("TEST");
        }
    }
    private void drawLeftLines(Canvas canvas) {

        for (Connection connection : leftItems){
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth()/2);
            int y1 = (int) (parent.getY() + parent.getHeight()/2);
            int x2 = (int) (item.getX() + item.getWidth()/2);
            int y2 = (int) (item.getY() + item.getHeight()/2);
            //int radius = (int) (((item.getX() + item.getWidth()) - (parent.getX()))/4);
            int radius = 0;
            drawCurvedArrowLeft(x1, y1, x2, y2, radius, canvas,
                    (item.getY()+item.getHeight()) < (parent.getY() + parent.getHeight()),
                    item.getConnectionByParent(parent).getConnectionTextMessage(), connection);
        }
    }
    private void drawRightLines(Canvas canvas) {

        for (Connection connection : rightItems){
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth()/2);
            int y1 = (int) (parent.getY() + parent.getHeight()/2);
            int x2 = (int) (item.getX() + item.getWidth()/2);
            int y2 = (int) (item.getY() + item.getHeight()/2);
            //int radius = (int) (((parent.getX() + parent.getWidth()) - (item.getX()))/4);
            int radius = 0;
            drawCurvedArrowRight(x1, y1, x2, y2, radius, canvas,
                    (item.getY()+item.getHeight()) < (parent.getY() + parent.getHeight()),
                    item.getConnectionByParent(parent).getConnectionTextMessage(), connection);
        }
    }
    private void drawBottomLines(Canvas canvas) {

        for (Connection connection : bottomItems){
            Item item = connection.getItem();
            Item parent = connection.getParent();
            int x1 = (int) (parent.getX() + parent.getWidth()/2);
            int y1 = (int) (parent.getY() + parent.getHeight()/2);
            int x2 = (int) (item.getX() + item.getWidth()/2);
            int y2 = (int) (item.getY() + item.getHeight()/2);
            //int radius = (int) (((parent.getY() + parent.getHeight()) - (item.getY()))/4);
            int radius = 0;
            drawCurvedArrowBottom(x1, y1, x2, y2, radius, canvas,
                    item.getX() > parent.getX(), item.getConnectionByParent(parent).getConnectionTextMessage(), connection);
        }
    }
    private void drawCurvedArrowTop(int x1, int y1, int x2, int y2, int curveRadius, Canvas canvas, boolean right,
                                    ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int y1_from_circ  = y1 - radius;
        int y2_to_trg = y2 + arrowSize + argExt;
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1_from_circ + ((y2_to_trg - y1_from_circ) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1_from_circ;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (right){
            pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));
        }else{
            pointX        = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY - curveRadius * Math.sin(angleRadians));
        }

//        path.moveTo(x1, y1_from_circ);
//        path.cubicTo(x1,y1_from_circ,pointX, pointY, x2, y2_to_trg);
//        path.moveTo(x2, y2_to_trg);
//        path.lineTo(x2, y2_to_trg - argExt);
        float yMid = (float) (y1 + y2)/2;
        path.moveTo(x1, y1);
        path.lineTo(x1, yMid);
        path.lineTo(x2, yMid);
        path.lineTo(x2, y2);

        canvas.drawPath(path, paint);

        Paint paint2  = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2-arrowSize/2, y2+arrowSize);
        Point point2 = new Point(x2+arrowSize/2,y2+arrowSize);
        Point point3 = new Point(x2, y2);

        Path path2 = new Path();
        path2.moveTo(x2, y2_to_trg);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();
        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1, y1-radius, radius, paint2);

        if (connectionTextMessage != null){
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup)connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2 - connectionTextMessage.getWidth()/2);
            connectionTextMessage.setY(y2_to_trg);

        }else if (argExt > 0){
            canvas.drawCircle(x2, y2_to_trg+radius, radius, paint2);
        }


    }
    private void drawCurvedArrowLeft(int x1, int y1, int x2, int y2, int curveRadius,
                                     Canvas canvas, boolean top, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int x1_from_circ  = x1 - radius;
        int x2_to_trg = x2 + arrowSize + argExt;
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX            = x1_from_circ + ((x2_to_trg - x1_from_circ) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1_from_circ;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (top){
            pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));
        }else{
            pointX        = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY - curveRadius * Math.sin(angleRadians));
        }

//        path.moveTo(x1_from_circ, y1);
//        path.cubicTo(x1_from_circ,y1,pointX, pointY, x2_to_trg, y2);
//        path.moveTo(x2_to_trg, y2);
//        path.lineTo(x2_to_trg - argExt, y2);
//        path.close();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);

        canvas.drawPath(path, paint);

        Paint paint2  = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2+arrowSize, y2-arrowSize/2);
        Point point2 = new Point(x2+arrowSize,y2+arrowSize/2);
        Point point3 = new Point(x2, y2);

        path.moveTo(x2_to_trg,y2);
        path.lineTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        path.close();

        Path path2 = new Path();
        path2.moveTo(x2_to_trg, y2);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();

        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1-radius, y1, radius, paint2);

        if (connectionTextMessage != null){
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup)connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2_to_trg);
            connectionTextMessage.setY(y2 - connectionTextMessage.getHeight()/2);

        }else if (argExt > 0){
            canvas.drawCircle(x2_to_trg, y2, radius, paint2);
        }



    }
    private void drawCurvedArrowRight(int x1, int y1, int x2, int y2, int curveRadius,
                                      Canvas canvas, boolean top, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int x1_from_circ  = x1 + radius;
        int x2_to_trg = x2 - arrowSize - argExt;
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX            = x1_from_circ + ((x2_to_trg - x1_from_circ) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1_from_circ;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (top){
            pointX        = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY - curveRadius * Math.sin(angleRadians));
        }else{
            pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));
        }

//        path.moveTo(x1_from_circ, y1);
//        path.cubicTo(x1_from_circ,y1,pointX, pointY, x2_to_trg, y2);
//        path.moveTo(x2_to_trg, y2);
//        path.lineTo(x2_to_trg + argExt, y2);
//        path.close();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);

        canvas.drawPath(path, paint);

        Paint paint2  = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2-arrowSize, y2-arrowSize/2);
        Point point2 = new Point(x2-arrowSize,y2+arrowSize/2);
        Point point3 = new Point(x2, y2);

        path.moveTo(x2_to_trg,y2);
        path.lineTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        path.close();

        Path path2 = new Path();
        path2.moveTo(x2_to_trg, y2);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();

        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1+radius, y1, radius, paint2);

        if (connectionTextMessage != null){
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup)connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2_to_trg - connectionTextMessage.getWidth());
            connectionTextMessage.setY(y2 - connectionTextMessage.getHeight()/2);

        }else if (argExt > 0){
            canvas.drawCircle(x2_to_trg, y2, radius, paint2);
        }



    }
    private void drawCurvedArrowBottom(int x1, int y1, int x2, int y2, int curveRadius,
                                       Canvas canvas, boolean right, ConnectionTextMessage connectionTextMessage, Connection connection) {

        int radius = connectionCircRadius;
        int arrowSize = connectionArrowSize;
        int lineWidth = connectionWidth;
        int argExt = connectionArgSize;
        String color = connectionColor;

        if (connection.getCircRadius() > 0)
            radius = connection.getCircRadius();
        else if (connection.getArrowSize() > 0)
            arrowSize = connection.getArrowSize();
        else if (connection.getWidth() > 0)
            lineWidth = connection.getWidth();
        else if (connection.getArgExt() > 0)
            argExt = connection.getArgExt();

        int y1_from_circ  = y1 + radius;
        int y2_to_trg = y2 - arrowSize - argExt;
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(Color.parseColor(color));
        paint.setStrokeCap(Paint.Cap.ROUND);

        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1_from_circ + ((y2_to_trg - y1_from_circ) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1_from_circ;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX, pointY;
        if (right){
            pointX        = (float) (midX - curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY - curveRadius * Math.sin(angleRadians));
        }else{
            pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
            pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));
        }

//        path.moveTo(x1, y1_from_circ);
//        path.cubicTo(x1,y1_from_circ,pointX, pointY, x2, y2_to_trg);
//        path.moveTo(x2, y2_to_trg);
//        path.lineTo(x2, y2);
//        path.close();
        float yMid = (float) (y1 + y2)/2;
        path.moveTo(x1, y1);
        path.lineTo(x1, yMid);
        path.lineTo(x2, yMid);
        path.lineTo(x2, y2);
        canvas.drawPath(path, paint);

        Paint paint2  = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(lineWidth);
        paint2.setColor(Color.parseColor(color));
        paint2.setStrokeCap(Paint.Cap.ROUND);

        Point point1 = new Point(x2-arrowSize/2, y2-arrowSize);
        Point point2 = new Point(x2+arrowSize/2,y2-arrowSize);
        Point point3 = new Point(x2, y2);

        Path path2 = new Path();
        path2.moveTo(x2, y2_to_trg);
        path2.lineTo(point1.x, point1.y);
        path2.lineTo(point2.x, point2.y);
        path2.lineTo(point3.x, point3.y);
        path2.lineTo(point1.x, point1.y);
        path2.close();
        canvas.drawPath(path2, paint2);

        canvas.drawCircle(x1, y1+radius, radius, paint2);

        if (connectionTextMessage != null){
            if (connectionTextMessage.getParent() != null)
                ((ViewGroup)connectionTextMessage.getParent()).removeView(connectionTextMessage);
            this.addView(connectionTextMessage);
            connectionTextMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            connectionTextMessage.setX(x2 - connectionTextMessage.getWidth()/2);
            connectionTextMessage.setY(y2_to_trg - connectionTextMessage.getHeight());

        }else if (argExt > 0){
            canvas.drawCircle(x2, y2_to_trg, radius, paint2);
        }

    }

    //Adding custom connection (straight line with 2 circles)
    public void addCustomConnection(Item item1, int position1, Item item2, int position2,ConnectionTextMessage connectionTextMessage,
                                    int width, String color, int circRadius1, int circRadius2){
        CustomConnection customConnection = new CustomConnection(item1, item2, connectionTextMessage, width, circRadius1,
                circRadius2, color, position1, position2);
        customConnections.add(customConnection);

    }
    public void drawCustomConnection(Canvas canvas){

        for (CustomConnection customConnection : customConnections){



            Item item1 = customConnection.getItem1();
            int position1 = customConnection.getPosition1();
            Item item2 = customConnection.getItem2();
            int position2 = customConnection.getPosition2();
            int custom_width = customConnection.getWidth();
            String custom_color = customConnection.getColor();
            int custom_circRadius2 = customConnection.getCircRadius2();
            int custom_circRadius1 = customConnection.getCircRadius1();





            Point start_point = new Point(0,0), end_point = new Point(0,0);
            if (position1 == ItemLocation.RIGHT){
                start_point = new Point((int) item1.getX()+item1.getWidth()+custom_circRadius1, (int) item1.getY()+item1.getHeight()/2);

            }
            else if (position1 == ItemLocation.TOP){
                start_point = new Point((int) item1.getX()+item1.getWidth()/2, (int) item1.getY()-custom_circRadius1);

            }
            else if (position1 == ItemLocation.LEFT){
                start_point = new Point((int) item1.getX()-custom_circRadius1, (int) item1.getY()+item1.getHeight()/2);

            }
            else if (position1 == ItemLocation.BOTTOM){
                start_point = new Point((int) item1.getX()+item1.getWidth()/2, (int) item1.getY()+item1.getHeight()+custom_circRadius1);

            }

            if (position2 == ItemLocation.RIGHT){

                end_point = new Point((int) item2.getX()+item2.getWidth()+custom_circRadius2, (int) item2.getY()+item2.getHeight()/2);


            }
            else if (position2 == ItemLocation.TOP){


                end_point = new Point((int) item2.getX()+item2.getWidth()/2, (int) item2.getY()-custom_circRadius2);

            }
            else if (position2 == ItemLocation.LEFT){


                end_point = new Point((int) item2.getX()-custom_circRadius2, (int) item2.getY()+item2.getHeight()/2);

            }
            else if (position2 == ItemLocation.BOTTOM){


                end_point = new Point((int) item2.getX()+item2.getWidth()/2, (int) item2.getY()+item2.getHeight()+custom_circRadius2);

            }


            Paint paint  = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(custom_width);
            paint.setColor(Color.parseColor(custom_color));
            paint.setStrokeCap(Paint.Cap.ROUND);

            paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));

            Path path = new Path();
            path.moveTo(start_point.x, start_point.y);
            path.lineTo(end_point.x, end_point.y);
            path.close();
            canvas.drawLine(start_point.x, start_point.y, end_point.x, end_point.y, paint);


            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(start_point.x, start_point.y, custom_circRadius1, paint);
            canvas.drawCircle(end_point.x, end_point.y, custom_circRadius2, paint);

        }

        invalidate();

    }

    //Setting the listener for the view's items

    public void  setOnItemClicked(OnItemClicked onItemClicked){
        this.onItemClicked = onItemClicked;
    }
}
