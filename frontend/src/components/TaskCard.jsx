import './TaskCard.css';

const TaskCard = ({ task, onComplete, onDelete, projectStatus }) => {
  const isCompleted = task.completed;
  const canComplete = projectStatus === 'ACTIVE' && !isCompleted;

  return (
    <div className={`task-card ${isCompleted ? 'completed' : ''}`}>
      <div className="task-main">
        <div className="task-checkbox">
          {isCompleted ? (
            <span className="check-icon">✓</span>
          ) : canComplete ? (
            <button onClick={onComplete} className="btn-complete" title="Complete task">
              ○
            </button>
          ) : (
            <span className="btn-complete disabled" title="Activate project first to complete tasks">
              ○
            </span>
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
      {!isCompleted && projectStatus === 'DRAFT' && (
        <span className="draft-badge">⏸ Activate project to complete</span>
      )}
    </div>
  );
};

export default TaskCard;
