import './TaskCard.css';

const TaskCard = ({ task, onComplete, onDelete }) => {
  const isCompleted = task.completed;

  return (
    <div className={`task-card ${isCompleted ? 'completed' : ''}`}>
      <div className="task-main">
        <div className="task-checkbox">
          {isCompleted ? (
            <span className="check-icon">✓</span>
          ) : (
            <button onClick={onComplete} className="btn-complete">
              ○
            </button>
          )}
        </div>
        <div className="task-info">
          <h4 className={isCompleted ? 'completed-text' : ''}>{task.title}</h4>
        </div>
        <button onClick={onDelete} className="btn-delete-task" title="Delete task">
          🗑️
        </button>
      </div>
      {isCompleted && <span className="completed-badge">✓ Completed</span>}
    </div>
  );
};

export default TaskCard;
